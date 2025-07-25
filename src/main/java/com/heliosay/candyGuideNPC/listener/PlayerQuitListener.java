package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.music.MusicManager;
import com.heliosay.candyGuideNPC.npc.GuideManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final GuideManager guideManager;
    private final MusicManager musicManager;
    public PlayerQuitListener(GuideManager guideManager, MusicManager musicManager){
        this.guideManager = guideManager;
        this.musicManager = musicManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        guideManager.removeGuideNpcForPlayer(event.getPlayer().getUniqueId());

        // MUSIC INTEGRATION
        if (musicManager != null && musicManager.isAvailable()) {
            musicManager.stopMusicForPlayer(event.getPlayer());
        }

    }




}
