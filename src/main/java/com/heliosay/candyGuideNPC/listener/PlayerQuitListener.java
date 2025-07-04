package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final NpcManager npcManager;

    public PlayerQuitListener(NpcManager npcManager){this.npcManager = npcManager;}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

    }
}
