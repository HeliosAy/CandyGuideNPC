package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensLoadListener implements Listener {
    private final CandyGuideNPC plugin;

    public CitizensLoadListener(CandyGuideNPC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        plugin.getLogger().info("Citizens is fully loaded. Now removing ghost NPCs...");
        plugin.getNpcHandler().removeGhostNPCs(plugin.getNpcConfig().getNpcName());
    }
}
