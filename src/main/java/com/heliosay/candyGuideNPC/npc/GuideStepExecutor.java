package com.heliosay.candyGuideNPC.npc;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.entity.MobSummoner;
import com.heliosay.candyGuideNPC.entity.PlayerMobManager;
import com.heliosay.candyGuideNPC.hologram.HologramManager;
import com.heliosay.candyGuideNPC.util.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuideStepExecutor {

    private final CandyGuideNPC plugin;
    private final PlayerMobManager playerMobManager;
    private final MobSummoner mobSummoner;
    private final ChatHelper chatHelper;
    private final Map<UUID, List<UUID>> playerCurrentTaskMobs;
    private final Map<UUID, BukkitRunnable> activeParticleTasks;
    private final Map<UUID, ArmorStand> activeHolograms = new ConcurrentHashMap<>();

    public GuideStepExecutor(CandyGuideNPC plugin, PlayerMobManager playerMobManager, MobSummoner mobSummoner, ChatHelper chatHelper) {
        this.plugin = plugin;
        this.playerMobManager = playerMobManager;
        this.mobSummoner = mobSummoner;
        this.chatHelper = chatHelper;
        this.playerCurrentTaskMobs = new ConcurrentHashMap<>();
        this.activeParticleTasks = new ConcurrentHashMap<>();
    }

    public void executeGeneralActions(Player player, GuideStep step) {
        chatHelper.clearChat(player);
        if (step.getMessages() != null && !step.getMessages().isEmpty()) {
            for (String message : step.getMessages()) {
                player.sendMessage(chatHelper.centerMessage(ChatColor.translateAlternateColorCodes('&',
                        message.replace("{player}", player.getName()))));
            }
        }
        if (step.getCommands() != null && !step.getCommands().isEmpty()) {
            for (String command : step.getCommands()) {
                String processedCommand = command.replace("{player}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }
        }
    }

    public void executeWaypointActions(Player player, GuideStep step) {
        if (step.getWaypointMessages() != null && !step.getWaypointMessages().isEmpty()) {
            chatHelper.clearChat(player);
            for (String message : step.getWaypointMessages()) {
                player.sendMessage(chatHelper.centerMessage(ChatColor.translateAlternateColorCodes('&',
                        message.replace("{player}", player.getName()))));
            }
        }
        if (step.getWaypointCommands() != null && !step.getWaypointCommands().isEmpty()) {
            for (String command : step.getWaypointCommands()) {
                String processedCommand = command.replace("{player}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }
        }
    }

    public void performMobSpawnActions(Player player, GuideStep step, Location spawnLocation) {
        List<UUID> currentStepMobs = playerCurrentTaskMobs.computeIfAbsent(
                player.getUniqueId(),
                k -> Collections.synchronizedList(new ArrayList<>())
        );

        synchronized (currentStepMobs) {
            for (UUID mobId : currentStepMobs) {
                Entity existingMob = plugin.getServer().getEntity(mobId);
                if (existingMob != null) {
                    existingMob.remove();
                }
            }
            currentStepMobs.clear();
        }

        if (step.getMobType() == null) {
            plugin.getLogger().warning("Mob type is null for step, skipping mob spawn for player " + player.getName());
            return;
        }

        for (int i = 0; i < step.getMobAmount(); i++) {
            Location mobSpawnLoc = spawnLocation != null ? spawnLocation : player.getLocation();
            Entity mob = mobSummoner.summonAndConfigureMob(mobSpawnLoc, step);
            if (mob != null) {
                if (mob instanceof Mob) {
                    ((Mob) mob).setTarget(player);
                }
                playerMobManager.registerPlayerSpecificMob(mob, player);
                synchronized (currentStepMobs) {
                    currentStepMobs.add(mob.getUniqueId());
                }
            } else {
                plugin.getLogger().warning("Mob oluşturulamadı veya yapılandırılamadı for player " + player.getName());
            }
        }

        if (step.getMobSpawnMessages() != null && !step.getMobSpawnMessages().isEmpty()) {
            if (step.getMessages() == null || step.getMessages().isEmpty()) {
                chatHelper.clearChat(player);
            }
            for (String message : step.getMobSpawnMessages()) {
                player.sendMessage(chatHelper.centerMessage(ChatColor.translateAlternateColorCodes('&',
                        message.replace("{player}", player.getName()))));
            }
        }
        if (step.getMobSpawnCommands() != null && !step.getMobSpawnCommands().isEmpty()) {
            for (String command : step.getMobSpawnCommands()) {
                String processedCommand = command.replace("{player}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }
        }
    }

    public void startParticleTask(Player player, GuideStep step) {
        Location particleLoc = step.getSpawnParticleLocation();
        Particle particleType = step.getSpawnParticleType();

        if (particleLoc == null || particleType == null || !player.getWorld().equals(particleLoc.getWorld())) {
            return;
        }

        stopParticleTask(player.getUniqueId());

        if (step.getParticleMessage() != null) {
            ArmorStand hologram = HologramManager.spawnHologram(particleLoc.clone(), step.getParticleMessage());
            if (hologram != null) {
                player.showEntity(plugin, hologram);
                activeHolograms.put(player.getUniqueId(), hologram);
            }
        }


        BukkitRunnable particleTask = new BukkitRunnable() {
            double height = 1.5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    activeParticleTasks.remove(player.getUniqueId());
                    return;
                }

                // Dikey çizgi
                for (double y = 0; y <= 1.2; y += 0.1) {
                    player.spawnParticle(particleType, particleLoc.clone().add(0, height - y, 0), 0);
                }

                // Ok başı (düz V)
                double baseY = height - 1.2 - 0.1;
                int steps = 6;
                double stepSize = 0.1;

                for (int i = 0; i < steps; i++) {
                    double offset = i * stepSize;

                    Location left = particleLoc.clone().add(-offset, baseY + offset, 0);
                    Location right = particleLoc.clone().add(offset, baseY + offset, 0);

                    player.spawnParticle(particleType, left, 0);
                    player.spawnParticle(particleType, right, 0);
                }
            }

        };

        activeParticleTasks.put(player.getUniqueId(), particleTask);
        particleTask.runTaskTimer(plugin, 0L, 20L);
    }


    public void stopParticleTask(UUID playerUUID) {
        BukkitRunnable particleTask = activeParticleTasks.remove(playerUUID);
        if (particleTask != null) {
            particleTask.cancel();
        }
        ArmorStand hologram = activeHolograms.remove(playerUUID);
        if (hologram != null) {
            HologramManager.removeHologram(hologram);
        }
    }

    public void removePlayerRelatedTasks(UUID playerUUID) {

        playerMobManager.removePlayerMobs(playerUUID);
        playerCurrentTaskMobs.remove(playerUUID);

        stopParticleTask(playerUUID);
    }

    public void cleanup() {
        for (UUID playerUUID : new HashSet<>(activeParticleTasks.keySet())) {
            stopParticleTask(playerUUID);
        }
        activeParticleTasks.clear();

        playerCurrentTaskMobs.clear();
        plugin.getLogger().info("GuideStepExecutor'a ait tüm aktif mob ve partikül taskları temizlendi!");
    }
}