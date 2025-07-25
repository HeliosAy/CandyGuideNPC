package com.heliosay.candyGuideNPC.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HologramManager {

    private static Plugin plugin;

    public static void setPlugin(Plugin p) {
        plugin = p;
    }

    /**
     * Belirtilen konumda, belirli bir mesajla bir hologram (ArmorStand) oluşturur.
     * Bu hologram başlangıçta tüm oyuncular için görünmezdir.
     *
     * @param baseLocation Hologramın spawn olacağı temel konum.
     * @param message Hologramda görüntülenecek mesaj.
     * @return Oluşturulan ArmorStand objesi veya hata durumunda null.
     */
    public static ArmorStand spawnHologram(Location baseLocation, String message) {

        Location holoLoc = baseLocation.clone().add(0, 1.5, 0);
        World world = baseLocation.getWorld();


        ArmorStand hologram = world.spawn(holoLoc, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setMarker(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', message));
            armorStand.setSmall(true);
            armorStand.setPersistent(false);

            armorStand.setVisibleByDefault(false);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hideEntity(plugin, armorStand);
            }
        });

        return hologram;
    }

    /**
     * Belirtilen hologramı sunucudan kaldırır.
     *
     * @param hologram Kaldırılacak ArmorStand objesi.
     */
    public static void removeHologram(ArmorStand hologram) {
        if (hologram != null && !hologram.isDead()) {
            hologram.remove();
        }
    }
}