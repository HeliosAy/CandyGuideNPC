package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerShiftListener implements Listener {
    private final NpcManager npcManager;

    public PlayerShiftListener(NpcManager npcManager){this.npcManager = npcManager;}

    @EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent event){

    }
}
