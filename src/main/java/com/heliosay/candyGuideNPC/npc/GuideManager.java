package com.heliosay.candyGuideNPC.npc;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.entity.PlayerMobManager;
import com.heliosay.candyGuideNPC.entity.MobSummoner;
import com.heliosay.candyGuideNPC.music.MusicManager;
import com.heliosay.candyGuideNPC.util.ChatHelper;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.heliosay.candyGuideNPC.util.ChatHelper.colorize;

public class GuideManager {

    private final CandyGuideNPC plugin;
    private final NpcConfig npcConfig;
    private final PlayerMobManager playerMobManager;
    private final MusicManager musicManager;
    private final ChatHelper chatHelper;
    private final NpcHandler npcHandler;
    private final GuideStepExecutor stepExecutor;

    private final Map<UUID, Integer> playerProgress = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastInteraction = new ConcurrentHashMap<>();
    private final Set<UUID> playersWaitingForInput = Collections.synchronizedSet(new HashSet<>());
    private final Map<UUID, Boolean> isNpcNavigating = new ConcurrentHashMap<>();


    public GuideManager(CandyGuideNPC plugin, NpcConfig npcConfig, PlayerMobManager playerMobManager, MusicManager musicManager) {
        this.plugin = plugin;
        this.npcConfig = npcConfig;
        this.playerMobManager = playerMobManager;
        this.musicManager = musicManager;
        this.chatHelper = new ChatHelper(npcConfig.getEmptyLines());
        this.npcHandler = new NpcHandler(plugin, npcConfig);
        this.stepExecutor = new GuideStepExecutor(plugin, playerMobManager, new MobSummoner(plugin), chatHelper);
    }

    public void spawnGuideNpcForPlayer(Player player) {
        if (!npcConfig.isEnabled()) return;
        UUID uuid = player.getUniqueId();

        // Önceki NPC'yi temizle (varsa)
        removeGuideNpcForPlayer(uuid);

        npcHandler.spawnNpc(player);

        playerProgress.put(uuid, 0);
        lastInteraction.put(uuid, 0L);
        isNpcNavigating.put(uuid, false);

        // Hoş geldin mesajı için
        new BukkitRunnable() {
            @Override
            public void run() {
                sendWelcomeMessage(player);
            }
        }.runTaskLater(plugin, 20L);
    }

    private void sendWelcomeMessage(Player player) {
        List<String> welcomeMessages = npcConfig.getWelcomeMessages();

        if (!welcomeMessages.isEmpty()) {
            chatHelper.clearChat(player);
            for (String message : welcomeMessages) {
                player.sendMessage(chatHelper.centerMessage(colorize(
                        message.replace("{player}", player.getName()))));
            }
            sendContinueMessage(player);
            playersWaitingForInput.add(player.getUniqueId());
        }
    }

    public void handleInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        NPC npc = npcHandler.getNpc(uuid);

        if (npc == null || !npc.isSpawned() || !npc.getEntity().equals(event.getRightClicked())) {
            return;
        }
        event.setCancelled(true);
        if (playersWaitingForInput.contains(uuid)) {
            processNextStep(player);
        }
    }

    public void handleShiftClick(Player player) {
        UUID uuid = player.getUniqueId();
        NPC npc = npcHandler.getNpc(uuid);

        if (npc == null || !npc.isSpawned() || !player.getLocation().getWorld().equals(npc.getStoredLocation().getWorld()) ||
                player.getLocation().distance(npc.getStoredLocation()) > 10.0) {
            return;
        }

        if (playersWaitingForInput.contains(uuid)) {
            processNextStep(player);
        }
    }

    private void processNextStep(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - lastInteraction.getOrDefault(uuid, 0L) < npcConfig.getCooldown()) {
            return;
        }
        lastInteraction.put(uuid, now);

        NPC npc = npcHandler.getNpc(uuid);
        if (npc == null || !npc.isSpawned()) return;

        if (npcConfig.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.3f);
        }

        int progress = playerProgress.getOrDefault(uuid, 0);
        List<GuideStep> steps = npcConfig.getGuideSteps();
        if (progress >= steps.size()) {
            finishGuide(player);
            return;
        }

        GuideStep step = steps.get(progress);
        if (step.getPermission() != null && !player.hasPermission(step.getPermission())) {
            String msg = step.getPermissionMessage() != null ? step.getPermissionMessage() : "&cYou do not have permission to continue this step.";
            player.sendMessage(colorize(msg));
            return;
        }

        // Particle task'ını durdur (Önceki adımdan kalma varsa)
        stepExecutor.stopParticleTask(player.getUniqueId());

        executeCurrentStep(player, npc, step);
    }

    private void executeCurrentStep(Player player, NPC npc, GuideStep step) {
        playersWaitingForInput.remove(player.getUniqueId());

        if (npcConfig.getNpcType() == EntityType.PLAYER) {
            if (npc.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.swingMainHand();
            }
        }


        boolean hasGeneralMessages = step.getMessages() != null && !step.getMessages().isEmpty();
        boolean hasGeneralCommands = step.getCommands() != null && !step.getCommands().isEmpty();
        boolean hasWaypoint = step.getLocation() != null;
        boolean hasMobSpawnConfig = step.getMobType() != null;

        // Genel mesajlar ve komutlar
        if (hasGeneralMessages || hasGeneralCommands) {
            stepExecutor.executeGeneralActions(player, step);
        }

        // Eğer sadece genel mesaj/komut varsa ve waypoint/mob yoksa
        if ((hasGeneralMessages || hasGeneralCommands) && !hasWaypoint && !hasMobSpawnConfig) {
            sendContinueMessage(player);
            playersWaitingForInput.add(player.getUniqueId());
            playerProgress.compute(player.getUniqueId(), (uuid, progress) -> progress + 1);
            return;
        }

        // Waypoint yoksa ama mob spawn varsa
        if (!hasWaypoint && hasMobSpawnConfig) {
            stepExecutor.performMobSpawnActions(player, step, step.getMobSpawnLocation());
            sendContinueMessage(player);
            playersWaitingForInput.add(player.getUniqueId());
            playerProgress.compute(player.getUniqueId(), (uuid, progress) -> progress + 1);
            return;
        }

        // Waypoint varsa
        if (hasWaypoint) {
            Location target = step.getLocation();
            if (target.getWorld() != null) {
                chatHelper.clearChat(player);
                player.sendMessage(chatHelper.centerMessage(colorize(npcConfig.getFollowMessage())));

                // NpcHandler ile navigasyonu başlat ve callback sağla
                isNpcNavigating.put(player.getUniqueId(), true);

                npcHandler.startNpcNavigation(player, npc, target, () -> {
                    // Waypoint'e varıldığında yapılacaklar
                    stepExecutor.executeWaypointActions(player, step);

                    isNpcNavigating.put(player.getUniqueId(), false);

                    if (hasMobSpawnConfig) {
                        stepExecutor.performMobSpawnActions(player, step, step.getMobSpawnLocation() != null ? step.getMobSpawnLocation() : target);
                    }

                    // Waypoint'e varıldığında particle
                    if (step.getSpawnParticleType() != null && step.getSpawnParticleLocation() != null) {
                        stepExecutor.startParticleTask(player, step);
                    }

                    sendContinueMessage(player);
                    playersWaitingForInput.add(player.getUniqueId());
                    playerProgress.compute(player.getUniqueId(), (uuid, progress) -> progress + 1);
                });

            } else {
                player.sendMessage("§cTarget location is invalid!");
                playerProgress.compute(player.getUniqueId(), (uuid, progress) -> progress + 1);
                processNextStep(player);
            }
        } else if (!hasGeneralMessages && !hasGeneralCommands && !hasMobSpawnConfig) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    processNextStep(player);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    private void finishGuide(Player player) {
        chatHelper.clearChat(player);
        List<String> finishMessages = npcConfig.getFinishMessages();
        for (String message : finishMessages) {
            player.sendMessage(chatHelper.centerMessage(colorize(
                    message.replace("{player}", player.getName()))));
        }
        if (npcConfig.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        removeGuideNpcForPlayer(player.getUniqueId());

        player.setInvisible(false);
        musicManager.stopMusicForPlayer(player);
        player.teleport(npcConfig.getGuideCompletedTeleportLocation());
    }

    private void sendContinueMessage(Player player) {
        String continueMsg = npcConfig.getContinueMessage();
        player.sendMessage("");
        player.sendMessage(chatHelper.centerMessage(colorize(continueMsg)));
        player.sendMessage("");
    }

    public void removeGuideNpcForPlayer(UUID uuid) {
        npcHandler.removeNpc(uuid);
        stepExecutor.removePlayerRelatedTasks(uuid);

        playerProgress.remove(uuid);
        lastInteraction.remove(uuid);
        playersWaitingForInput.remove(uuid);
        isNpcNavigating.remove(uuid);
    }

    public void cleanup() {
        npcHandler.cleanup();
        stepExecutor.cleanup();

        playerProgress.clear();
        lastInteraction.clear();
        playersWaitingForInput.clear();
        isNpcNavigating.clear();

        playerMobManager.cleanup();

        plugin.getLogger().info("GuideManager and other services cleaned up!");
    }

    public CandyGuideNPC getPlugin() {
        return plugin;
    }
}