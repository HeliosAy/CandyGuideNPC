package com.heliosay.candyGuideNPC.listener.player;

import com.heliosay.candyGuideNPC.npc.NpcConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;


public class PlayerAntiCraftListener implements Listener {
    private NpcConfig npcConfig;

    public PlayerAntiCraftListener(NpcConfig npcConfig){
        this.npcConfig = npcConfig;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        String worldName = player.getWorld().getName();

        if (player.isOp() || player.hasPermission("guidenpc.admin")){
            return;
        }

        if (worldName.equalsIgnoreCase(npcConfig.getGuideWorld())) {
            event.setCancelled(true);
        }
    }

}
