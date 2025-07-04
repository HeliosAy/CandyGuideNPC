package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NpcInteractListener implements Listener {
    private NpcManager npcManager;

    public  NpcInteractListener(NpcManager npcManager){this.npcManager = npcManager;}

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent event){

    }

}
