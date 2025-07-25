package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.GuideManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NpcInteractListener implements Listener {
    private GuideManager guideManager;

    public NpcInteractListener(GuideManager guideManager) {
        this.guideManager = guideManager;
    }

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent event) {
        guideManager.handleInteract(event);
    }

}
