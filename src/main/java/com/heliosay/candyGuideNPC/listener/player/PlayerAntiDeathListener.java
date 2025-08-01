package com.heliosay.candyGuideNPC.listener.player;

import com.heliosay.candyGuideNPC.npc.NpcConfig;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerAntiDeathListener implements Listener {
    private NpcConfig npcConfig;

    public PlayerAntiDeathListener(NpcConfig npcConfig){
        this.npcConfig = npcConfig;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (!player.getWorld().getName().equalsIgnoreCase(npcConfig.getGuideWorld())){
            return;
        }

        double finalHealth = player.getHealth() - event.getFinalDamage();

        if (finalHealth <= 0) {
            event.setCancelled(true);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }
    }
}
