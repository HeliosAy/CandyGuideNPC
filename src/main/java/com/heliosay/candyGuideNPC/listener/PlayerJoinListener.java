package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.music.MusicManager;
import com.heliosay.candyGuideNPC.npc.GuideManager;
import com.heliosay.candyGuideNPC.npc.NpcConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
    private final GuideManager guideManager;
    private final NpcConfig npcConfig;
    private final MusicManager musicManager;

    public PlayerJoinListener(GuideManager guideManager, NpcConfig npcConfig, MusicManager musicManager){
        this.guideManager = guideManager;
        this.npcConfig = npcConfig;
        this.musicManager = musicManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player.hasPermission(npcConfig.getCompletedGuidePermission())){
                    player.teleport(npcConfig.getGuideCompletedTeleportLocation());
                } else {
                    player.teleport(npcConfig.getGuideStartTeleportLocation());
                    guideManager.spawnGuideNpcForPlayer(player);

                    if (musicManager != null && musicManager.isAvailable()) {
                        musicManager.playMusicToPlayer(player, "guide_theme");
                    }
                }
            }
        }.runTaskLater(guideManager.getPlugin(), 20L);
    }
}