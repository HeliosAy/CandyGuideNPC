package com.heliosay.candyGuideNPC.entity;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.npc.GuideStep;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class MobSummoner {

    private final CandyGuideNPC plugin;

    public MobSummoner(CandyGuideNPC plugin) {
        this.plugin = plugin;
    }

    /*
     * Verilen Step bilgilerine göre bir mob oluşturur, özelliklerini ayarlar ve ekipmanını giydirir.
     */
    public Entity summonAndConfigureMob(Location spawnLoc, GuideStep step) {
        if (spawnLoc == null || spawnLoc.getWorld() == null) {
            plugin.getLogger().warning("MobSummoner: Geçersiz spawn konumu. Mob oluşturulamadı.");
            return null;
        }

        Entity mob = spawnLoc.getWorld().spawnEntity(spawnLoc, step.getMobType());

        if (step.getMobName() != null && !step.getMobName().isEmpty()) {
            mob.setCustomName(ChatColor.translateAlternateColorCodes('&', step.getMobName()));
            mob.setCustomNameVisible(step.isMobNameVisible());
        }
        mob.setGlowing(step.isMobGlowing());
        mob.setSilent(step.isMobSilent());
        mob.setPersistent(false);

        if (mob instanceof Mob) {
            Mob hostileMob = (Mob) mob;
        }

        if (mob instanceof LivingEntity) {
            LivingEntity livingMob = (LivingEntity) mob;
            if (step.getMobEquipment() != null) {
                Map<String, ItemStack> equipment = step.getMobEquipment();

                if (equipment.containsKey("head")) {
                    ItemStack head = equipment.get("head");
                    if (head.getType() == Material.PLAYER_HEAD && step.getSkullTexture() != null && !step.getSkullTexture().isEmpty()) {
                        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                        if (skullMeta != null) {
                            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                            PlayerTextures textures = profile.getTextures();
                            try {
                                textures.setSkin(new URL("http://textures.minecraft.net/texture/" + step.getSkullTexture()));
                            } catch (MalformedURLException e) {
                                plugin.getLogger().warning("MobSummoner: Geçersiz skull texture URL: " + step.getSkullTexture() + " - " + e.getMessage());
                            }
                            profile.setTextures(textures);

                            skullMeta.setOwnerProfile(profile);
                            head.setItemMeta(skullMeta);
                        } else {
                            plugin.getLogger().warning("MobSummoner: SkullMeta null. Başlık itemi PLAYER_HEAD değil veya meta verisi alınamadı.");
                        }
                    }
                    livingMob.getEquipment().setHelmet(head);
                }
                if (equipment.containsKey("chest")) livingMob.getEquipment().setChestplate(equipment.get("chest"));
                if (equipment.containsKey("legs")) livingMob.getEquipment().setLeggings(equipment.get("legs"));
                if (equipment.containsKey("feet")) livingMob.getEquipment().setBoots(equipment.get("feet"));
                if (equipment.containsKey("main_hand")) livingMob.getEquipment().setItemInMainHand(equipment.get("main_hand"));
                if (equipment.containsKey("off_hand")) livingMob.getEquipment().setItemInOffHand(equipment.get("off_hand"));
            }
        }
        return mob;
    }
}