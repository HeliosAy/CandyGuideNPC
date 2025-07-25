 package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.entity.PlayerMobManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.UUID;

 public class MobTargetListener implements Listener {

    private final CandyGuideNPC plugin;
    private final PlayerMobManager playerMobManager;

    public MobTargetListener(CandyGuideNPC plugin, PlayerMobManager playerMobManager) {
        this.plugin = plugin;
        this.playerMobManager = playerMobManager;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity mob = (LivingEntity) event.getEntity();
        LivingEntity target = event.getTarget();

        if (target == null || !(target instanceof Player)) {
            return;
        }

        Player targetedPlayer = (Player) target;
        if (playerMobManager.playerSpecificMobs.containsKey(mob.getUniqueId())) {
            UUID ownerUUID = playerMobManager.playerSpecificMobs.get(mob.getUniqueId());
            if (!targetedPlayer.getUniqueId().equals(ownerUUID) && plugin.getServer().getPlayer(ownerUUID) != null) {
                event.setCancelled(true);
            }
        }
    }
}