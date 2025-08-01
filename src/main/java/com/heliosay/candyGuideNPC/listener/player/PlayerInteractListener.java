package com.heliosay.candyGuideNPC.listener.player;

import com.heliosay.candyGuideNPC.npc.GuideManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractListener implements Listener {
    private GuideManager guideManager;

    public PlayerInteractListener(GuideManager guideManager) {
        this.guideManager = guideManager;
    }

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent event) {
        guideManager.handleInteract(event);
    }

}
