package com.heliosay.candyGuideNPC.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PlayerMobManager {

    private final Plugin plugin;
    public final Map<UUID, UUID> playerSpecificMobs = new HashMap<>();

    public PlayerMobManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerPlayerSpecificMob(Entity mob, Player owner) {
        if (mob == null || owner == null) return;

        mob.setVisibleByDefault(false);
        mob.setPersistent(false);

        playerSpecificMobs.put(mob.getUniqueId(), owner.getUniqueId());

        owner.showEntity(plugin, mob);
    }

    public void removePlayerMobs(UUID playerUUID) {
        playerSpecificMobs.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(playerUUID)) {
                Entity mob = plugin.getServer().getEntity(entry.getKey());
                if (mob != null) {
                    Player owner = plugin.getServer().getPlayer(playerUUID);
                    if (owner != null && owner.isOnline()) {
                        owner.hideEntity(plugin, mob);
                    }
                    mob.remove();
                }
                return true;
            }
            return false;
        });
    }

    public void cleanup() {
        new HashSet<>(playerSpecificMobs.keySet()).forEach(mobUUID -> {
            Entity mob = plugin.getServer().getEntity(mobUUID);
            if (mob != null) {
                mob.remove();
            }
        });
        playerSpecificMobs.clear();
        plugin.getLogger().info("Tüm oyuncuya özel moblar temizlendi!");
    }

    public boolean isMobVisibleToPlayer(Player player, Entity mob) {
        return playerSpecificMobs.containsKey(mob.getUniqueId()) && playerSpecificMobs.get(mob.getUniqueId()).equals(player.getUniqueId());
    }
}