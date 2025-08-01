package com.heliosay.candyGuideNPC.listener.player;

import com.heliosay.candyGuideNPC.npc.GuideManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerShiftListener implements Listener {
    private final GuideManager guideManager;

    public PlayerShiftListener(GuideManager guideManager){this.guideManager = guideManager;}

    @EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent event) {
        if (event.isSneaking()){
            guideManager.handleShiftClick(event.getPlayer());
        }
    }
}
