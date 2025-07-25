package com.heliosay.candyGuideNPC.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class NpcConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public NpcConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload(){
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public Location getGuideStartTeleportLocation(){
        String worldName = config.getString("guide.start-teleport-location.world");
        if (worldName == null || Bukkit.getWorld(worldName)==null){
            plugin.getLogger().warning("Guide start teleport location is invalid or not loaded");
            if (Bukkit.getWorld("world") != null){
                worldName = "world";
            }else {
                worldName = Bukkit.getWorlds().get(0).getName();
            }
        }
        double x = config.getDouble("guide.start-teleport-location.x",100);
        double y = config.getDouble("guide.start-teleport-location.y",64);
        double z = config.getDouble("guide.start-teleport-location.z",100);
        float yaw = (float) config.getDouble("guide.start-teleport-location.yaw", 0.0);
        float pitch = (float) config.getDouble("guide.start-teleport-location.pitch", 0.0);

        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

    public Location getGuideCompletedTeleportLocation(){
        String worldName = config.getString("guide.completed-lobby-teleport-location.world");
        if (worldName == null || Bukkit.getWorld(worldName)==null){
            plugin.getLogger().warning("Guide completed lobby teleport location is invalid or not loaded");
            if (Bukkit.getWorld("world") != null){
                worldName = "world";
            }else {
                worldName = Bukkit.getWorlds().get(0).getName();
            }
        }
        double x = config.getDouble("guide.completed-lobby-teleport-location.x",100);
        double y = config.getDouble("guide.completed-lobby-teleport-location.y",64);
        double z = config.getDouble("guide.completed-lobby-teleport-location.z",100);
        float yaw = (float) config.getDouble("guide.completed-lobby-teleport-location.yaw", 0.0);
        float pitch = (float) config.getDouble("guide.completed-lobby-teleport-location.pitch", 0.0);

        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

    public String getCompletedGuidePermission(){
        return config.getString("guide.completed-guide-permission","guidenpc.completed");
    }

    public List<String> getAllowedCommandsDuringGuide() {
        List<String> allowed = config.getStringList("guide.allowed-commands-during-guide");
        if (allowed == null) {
            return Collections.emptyList();
        }
        List<String> lowerCaseAllowed = new ArrayList<>();
        for (String cmd : allowed) {
            lowerCaseAllowed.add(cmd.toLowerCase());
        }
        return lowerCaseAllowed;
    }


    public boolean isEnabled(){
        return config.getBoolean("npc.enabled");
    }

    public String getNpcName(){
        return config.getString("npc.name");
    }

    public EntityType getNpcType(){
        try {
            return EntityType.valueOf(config.getString("npc.type","villager").toUpperCase());
        }catch (Exception e){
            plugin.getLogger().warning("Invalid NPC type in config: " + config.getString("npc.type") + ". Defaulting to VILLAGER.");
            return EntityType.VILLAGER;
        }
    }

    public boolean isGlowing(){
        return config.getBoolean("npc.glowing");
    }

    public int getLookRange(){
        return config.getInt("npc.look-range");
    }

    public long getCooldown(){
        return config.getLong("npc.cooldown",3000);
    }

    public boolean isSoundEnabled(){
        return config.getBoolean("npc.sound-enabled",true);
    }

    public int getEmptyLines(){
        return config.getInt("chat.empty-lines",15);
    }

    public String getContinueMessage(){
        return config.getString("chat.continue-message","&7Devam etmek için &eShift &7tuşuna basın veya &eNPC'ye &7tıklayın");
    }

    public Location getSpawnLocation(){
        String worldName = config.getString("npc.spawn-location.world");
        if (worldName == null || Bukkit.getWorld(worldName) == null) {
            plugin.getLogger().warning("NPC spawn world '" + worldName + "' is invalid or not loaded.");
            if (Bukkit.getWorld("world") != null) {
                worldName = "world";
            } else {
                worldName = Bukkit.getWorlds().get(0).getName();
            }
        }
        double x = config.getDouble("npc.spawn-location.x",100);
        double y = config.getDouble("npc.spawn-location.y",64);
        double z = config.getDouble("npc.spawn-location.z",100);
        return new Location(Bukkit.getWorld(worldName),x,y,z);
    }


    public List<String> getWelcomeMessages() {
        return config.getStringList("messages.welcome");
    }

    public List<String> getFinishMessages(){
        return config.getStringList("messages.finish");
    }

    public List<GuideStep> getGuideSteps(){
        List<GuideStep> steps = new ArrayList<>();
        ConfigurationSection stepsSection = config.getConfigurationSection("guide-steps");
        if (stepsSection == null) {
            plugin.getLogger().warning("guide-steps section not found in config!");
            return steps;
        }

        for (String key : stepsSection.getKeys(false)){
            ConfigurationSection stepSection = stepsSection.getConfigurationSection(key);
            if (stepSection == null) continue;

            GuideStep step = new GuideStep();

            // Validation: En az bir aksiyon olmalı
            boolean hasMessages = stepSection.getStringList("messages") != null &&
                    !stepSection.getStringList("messages").isEmpty();
            boolean hasCommands = stepSection.getStringList("commands") != null &&
                    !stepSection.getStringList("commands").isEmpty();
            boolean hasLocation = stepSection.getConfigurationSection("location") != null;
            boolean hasMob = stepSection.getConfigurationSection("mob") != null;

            if (!hasMessages && !hasCommands && !hasLocation && !hasMob) {
                plugin.getLogger().warning("Step " + key + " has no actions defined. Skipping.");
                continue;
            }

            step.setMessages(stepSection.getStringList("messages"));
            step.setCommands(stepSection.getStringList("commands"));

            step.setPermission(stepSection.getString("permission"));
            step.setPermissionMessage(stepSection.getString("permission-message"));

            ConfigurationSection locationSection = stepSection.getConfigurationSection("location");
            if (locationSection != null) {
                String world = locationSection.getString("world");
                if (world == null || Bukkit.getWorld(world) == null) {
                    plugin.getLogger().warning("Invalid world for step " + key + " location: " + world + ". Defaulting to first available world.");
                    world = Bukkit.getWorlds().get(0).getName();
                }
                double x = locationSection.getDouble("x", 100);
                double y = locationSection.getDouble("y", 64);
                double z = locationSection.getDouble("z", 100);
                step.setLocation(new Location(Bukkit.getWorld(world), x, y, z));

                step.setWaypointMessages(locationSection.getStringList("messages"));
                step.setWaypointCommands(locationSection.getStringList("commands"));
            }

            ConfigurationSection mobSection = stepSection.getConfigurationSection("mob");
            if (mobSection != null) {
                try {
                    step.setMobType(EntityType.valueOf(mobSection.getString("type", "ZOMBIE").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Mob türü geçersiz. Steps: " + key + ": " + mobSection.getString("type") + ". Defaulting to ZOMBIE.");
                    step.setMobType(EntityType.ZOMBIE);
                }
                step.setMobAmount(Math.max(1, mobSection.getInt("amount", 1))); // En az 1 olsun
                step.setMobName(mobSection.getString("name"));
                step.setMobNameVisible(mobSection.getBoolean("name-visible", false));
                step.setMobGlowing(mobSection.getBoolean("glowing", false));
                step.setMobSilent(mobSection.getBoolean("silent", true));
                step.setSkullTexture(mobSection.getString("skull-texture"));

                ConfigurationSection mobLocationSection = mobSection.getConfigurationSection("location");
                if (mobLocationSection != null) {
                    String mobWorld = mobLocationSection.getString("world");
                    if (mobWorld == null || Bukkit.getWorld(mobWorld) == null) {
                        plugin.getLogger().warning("Invalid world for mob spawn location in step " + key + ": " + mobWorld + ". Defaulting to first available world.");
                        mobWorld = Bukkit.getWorlds().get(0).getName();
                    }
                    double mobX = mobLocationSection.getDouble("x", 100);
                    double mobY = mobLocationSection.getDouble("y", 64);
                    double mobZ = mobLocationSection.getDouble("z", 100);
                    step.setMobSpawnLocation(new Location(Bukkit.getWorld(mobWorld), mobX, mobY, mobZ));
                }

                ConfigurationSection equipmentSection = mobSection.getConfigurationSection("equipment");
                if (equipmentSection != null) {
                    Map<String, ItemStack> equipment = new HashMap<>();
                    for (String slot : equipmentSection.getKeys(false)) {
                        String materialName = equipmentSection.getString(slot);
                        if (materialName != null && !materialName.isEmpty()) {
                            try {
                                Material material = Material.valueOf(materialName.toUpperCase());
                                equipment.put(slot, new ItemStack(material));
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Invalid equipment type for mob: " + slot + " in step " + key + ": " + materialName);
                            }
                        }
                    }
                    step.setMobEquipment(equipment);
                } else {
                    step.setMobEquipment(Collections.emptyMap());
                }
                step.setMobSpawnMessages(mobSection.getStringList("messages"));
                step.setMobSpawnCommands(mobSection.getStringList("commands"));
            }

            ConfigurationSection particleSection = stepSection.getConfigurationSection("particle");
            if (particleSection != null) {
                String worldName = particleSection.getString("world");
                if (worldName != null && Bukkit.getWorld(worldName) != null) {
                    double x = particleSection.getDouble("x", 0);
                    double y = particleSection.getDouble("y", 0);
                    double z = particleSection.getDouble("z", 0);

                    step.setSpawnParticleLocation(new Location(Bukkit.getWorld(worldName), x, y, z));

                    String particleTypeName = particleSection.getString("type", "END_ROD");
                    try {
                        step.setSpawnParticleType(Particle.valueOf(particleTypeName.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Step " + key + ": Invalid particle type '" + particleTypeName + "'. Defaulting to END_ROD.");
                        step.setSpawnParticleType(Particle.END_ROD);
                    }
                    step.setParticleMessage(particleSection.getString("message"));
                } else {
                    plugin.getLogger().warning("Step " + key + ": Invalid world '" + worldName + "' for particle. Particle will not be spawned.");
                }
            }

            steps.add(step);
        }
        return steps;
    }
}