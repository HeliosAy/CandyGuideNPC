package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {
    private final NpcManager npcManager;

    public PlayerJoinListener(NpcManager npcManager){this.npcManager = npcManager;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Oyuncu tamamen yüklendikten sonra NPC spawn et
        new BukkitRunnable() {
            @Override
            public void run() {
                npcManager.spawnNpc(event.getPlayer());
            }
        }.runTaskLater(npcManager.getPlugin(), 20L);
    }
}
