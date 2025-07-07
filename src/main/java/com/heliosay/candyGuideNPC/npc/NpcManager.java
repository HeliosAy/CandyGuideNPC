package com.heliosay.candyGuideNPC.npc;


import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class NpcManager {

    private final JavaPlugin plugin;
    private final NPCRegistry registry;
    private final NpcConfig npcConfig;
    private final Map<UUID, NPC> playerNpcs = new HashMap<>();
    private final Map<UUID, Integer> playerProgress = new HashMap<>();
    private final Map<UUID, Long> lastInteraction = new HashMap<>();
    private final Set<UUID> playersWaitingForInput = new HashSet<>();

    public NpcManager(JavaPlugin plugin, NpcConfig npcConfig) {
        this.plugin = plugin;
        this.registry = CitizensAPI.getNPCRegistry();
        this.npcConfig = npcConfig;
    }

    public void spawnNpc(Player player) {
        if (!npcConfig.isEnabled()) return;

        UUID uuid = player.getUniqueId();

        // Zaten NPC varsa eski olanı temizle
        if (playerNpcs.containsKey(uuid)) {
            removeNpc(uuid);
        }

        Location spawnLoc = npcConfig.getSpawnLocation();
        if (spawnLoc == null || spawnLoc.getWorld() == null) {
            player.sendMessage(ChatColor.RED + "NPC spawn konumu geçersiz!");
            return;
        }

        NPC npc = registry.createNPC(npcConfig.getNpcType(),
                ChatColor.translateAlternateColorCodes('&', npcConfig.getNpcName()));
        npc.spawn(spawnLoc);

        // NPC özelliklerini ayarla
        npc.data().set(NPC.Metadata.GLOWING, npcConfig.isGlowing());
        npc.data().setPersistent(NPC.Metadata.SILENT, true);

        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.lookClose(true);
        lookClose.setRange(npcConfig.getlookRange());
        lookClose.setRealisticLooking(true);

        // Sadece belirli oyuncuya görünür yapma
        PlayerFilter filter = npc.getOrAddTrait(PlayerFilter.class);
        filter.clear();
        filter.setAllowlist();
        filter.addPlayer(uuid);
        filter.recalculate();

        playerNpcs.put(uuid, npc);
        playerProgress.put(uuid, 0);
        lastInteraction.put(uuid, 0L);

        // Hoş geldin mesajını göster
        new BukkitRunnable() {
            @Override
            public void run() {
                sendWelcomeMessage(player);
            }
        }.runTaskLater(plugin, 20L); // 1 saniye gecikme
    }

    private void sendWelcomeMessage(Player player) {
        List<String> welcomeMessages = npcConfig.getWelcomeMessages();
        if (!welcomeMessages.isEmpty()) {
            clearChat(player);
            for (String message : welcomeMessages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        message.replace("{player}", player.getName())));
            }
            sendContinueMessage(player);
            playersWaitingForInput.add(player.getUniqueId());
        }
    }

    public void handleInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        NPC npc = playerNpcs.get(uuid);
        if (npc == null || !npc.isSpawned() || !npc.getEntity().equals(event.getRightClicked())) {
            return;
        }

        event.setCancelled(true);
        processNext(player);
    }

    public void handleShiftClick(Player player) {
        UUID uuid = player.getUniqueId();
        if (playersWaitingForInput.contains(uuid)) {
            processNext(player);
        }
    }

    private void processNext(Player player) {
        UUID uuid = player.getUniqueId();

        // Tasklar arası Cooldown kontrolü
        long now = System.currentTimeMillis();
        if (now - lastInteraction.getOrDefault(uuid, 0L) < npcConfig.getCooldown()) {
            return;
        }
        lastInteraction.put(uuid, now);

        NPC npc = playerNpcs.get(uuid);
        if (npc == null || !npc.isSpawned()) return;

        // Eğer NPC hareket ediyr ve oyuncu etkileşşime giriyorsa bekle
        if (npc.getNavigator().isNavigating()) {
            player.sendMessage(ChatColor.YELLOW + "Lütfen bekleyin...");
            return;
        }

        int progress = playerProgress.getOrDefault(uuid, 0);
        List<GuideStep> steps = npcConfig.getGuideSteps();

        if (progress >= steps.size()) {
            finishGuide(player);
            return;
        }

        GuideStep step = steps.get(progress);
        executeStep(player, npc, step);

        playerProgress.put(uuid, progress + 1);
        playersWaitingForInput.remove(uuid);

        if (npcConfig.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
        }
    }

    private void executeStep(Player player, NPC npc, GuideStep step) {
        switch (step.getType()) {
            case MESSAGE:
                clearChat(player);
                for (String message : step.getMessages()) {
                    player.sendMessage(centerMessage(ChatColor.translateAlternateColorCodes('&',
                            message.replace("{player}", player.getName()))));
                }
                sendContinueMessage(player);
                playersWaitingForInput.add(player.getUniqueId());
                break;

            case WAYPOINT:
                Location target = step.getLocation();
                if (target != null && target.getWorld() != null) {
                    npc.getNavigator().setTarget(target);
                    clearChat(player);
                    player.sendMessage(centerMessage(ChatColor.GREEN + "Beni takip edin!"));

                    // Hedefe ulaştığında mesaj gönder
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!npc.getNavigator().isNavigating()) {
                                if (!step.getMessages().isEmpty()) {
                                    clearChat(player);
                                    for (String message : step.getMessages()) {
                                        player.sendMessage(centerMessage(ChatColor.translateAlternateColorCodes('&',
                                                message.replace("{player}", player.getName()))));
                                    }
                                }
                                sendContinueMessage(player);
                                playersWaitingForInput.add(player.getUniqueId());
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 20L, 20L);
                } else {
                    player.sendMessage(ChatColor.RED + "Hedef konum geçersiz!");
                }
                break;

            case COMMAND:
                for (String command : step.getCommands()) {
                    String processedCommand = command.replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
                }
                // Komut sonrası otomatik devam et
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        processNext(player);
                    }
                }.runTaskLater(plugin, 5L);
                break;
        }
    }

    private void finishGuide(Player player) {
        clearChat(player);
        List<String> finishMessages = npcConfig.getFinishMessages();
        for (String message : finishMessages) {
            player.sendMessage(centerMessage(ChatColor.translateAlternateColorCodes('&',
                    message.replace("{player}", player.getName()))));
        }

        if (npcConfig.isSoundEnabled()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        // NPC'yi kaldır
        new BukkitRunnable() {
            @Override
            public void run() {
                removeNpc(player.getUniqueId());
            }
        }.runTaskLater(plugin, 40L); // 2 saniye sonra kaldır
    }

    private void clearChat(Player player) {
        int emptyLines = npcConfig.getEmptyLines();
        for (int i = 0; i < emptyLines; i++) {
            player.sendMessage("");
        }
    }

    private String centerMessage(String message) {
        int chatWidth = 55; // Minecraft chat genişliği
        int messageLength = ChatColor.stripColor(message).length();

        if (messageLength >= chatWidth) return message;

        int spaces = (chatWidth - messageLength) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
        sb.append(message);
        return sb.toString();
    }

    private void sendContinueMessage(Player player) {
        String continueMsg = npcConfig.getContinueMessage();
        player.sendMessage("");
        player.sendMessage(centerMessage(ChatColor.translateAlternateColorCodes('&', continueMsg)));
        player.sendMessage("");
    }

    public void removeNpc(UUID uuid) {
        if (playerNpcs.containsKey(uuid)) {
            NPC npc = playerNpcs.remove(uuid);
            if (npc.isSpawned()) {
                npc.despawn();
            }
            npc.destroy();
        }
        playerProgress.remove(uuid);
        lastInteraction.remove(uuid);
        playersWaitingForInput.remove(uuid);
    }

    public void cleanup() {
        Set<UUID> uuids = new HashSet<>(playerNpcs.keySet());
        for (UUID uuid : uuids) {
            removeNpc(uuid);
        }
        plugin.getLogger().info("Tüm NPC'ler temizlendi!");
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
