package com.heliosay.candyGuideNPC.npc;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;

public class GuideStep {

    // Adım başlangıcında çalışanlar
    private List<String> messages;
    private List<String> commands;

    // Waypoint
    private Location location; // NPC'nin gideceği konum
    private List<String> waypointMessages; // Waypoint'e varınca
    private List<String> waypointCommands; // Waypoint'e varınca

    // Mob spawn ve özellikleri
    private EntityType mobType;
    private int mobAmount;
    private String mobName;
    private boolean mobNameVisible;
    private boolean mobGlowing;
    private boolean mobSilent;
    private Location mobSpawnLocation;
    private Map<String, ItemStack> mobEquipment;
    private String skullTexture;
    private List<String> mobSpawnMessages; // Moblar spawn olduktan sonra
    private List<String> mobSpawnCommands; // Moblar spawn olduktan sonra

    // Perm ve mesajlar
    private String permission;
    private String permissionMessage;

    // Particle özellikleri
    private Particle spawnParticleType;
    private Location spawnParticleLocation;
    private String particleMessage;

    // Genel Adım
    public List<String> getMessages() { return messages; }
    public void setMessages(List<String> messages) { this.messages = messages; }
    public List<String> getCommands() { return commands; }
    public void setCommands(List<String> commands) { this.commands = commands; }

    // Waypoint
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public List<String> getWaypointMessages() { return waypointMessages; }
    public void setWaypointMessages(List<String> waypointMessages) { this.waypointMessages = waypointMessages; }
    public List<String> getWaypointCommands() { return waypointCommands; }
    public void setWaypointCommands(List<String> waypointCommands) { this.waypointCommands = waypointCommands; }

    // Mob Spawn Özellikleri
    public EntityType getMobType() { return mobType; }
    public void setMobType(EntityType mobType) { this.mobType = mobType; }
    public int getMobAmount() { return mobAmount; }
    public void setMobAmount(int mobAmount) { this.mobAmount = mobAmount; }
    public String getMobName() { return mobName; }
    public void setMobName(@Nullable String mobName) { this.mobName = mobName; }
    public boolean isMobNameVisible() { return mobNameVisible; }
    public void setMobNameVisible(boolean mobNameVisible) { this.mobNameVisible = mobNameVisible; }
    public boolean isMobGlowing() { return mobGlowing; }
    public void setMobGlowing(boolean mobGlowing) { this.mobGlowing = mobGlowing; }
    public boolean isMobSilent() { return mobSilent; }
    public void setMobSilent(boolean mobSilent) { this.mobSilent = mobSilent; }

    public Location getMobSpawnLocation() { return mobSpawnLocation; }
    public void setMobSpawnLocation(Location mobSpawnLocation) { this.mobSpawnLocation = mobSpawnLocation; }

    public Map<String, ItemStack> getMobEquipment() { return mobEquipment; }
    public void setMobEquipment(Map<String, ItemStack> mobEquipment) { this.mobEquipment = mobEquipment; }
    public String getSkullTexture() { return skullTexture; }
    public void setSkullTexture(@Nullable String skullTexture) { this.skullTexture = skullTexture; }
    public List<String> getMobSpawnMessages() { return mobSpawnMessages; }
    public void setMobSpawnMessages(List<String> mobSpawnMessages) { this.mobSpawnMessages = mobSpawnMessages; }
    public List<String> getMobSpawnCommands() { return mobSpawnCommands; }
    public void setMobSpawnCommands(List<String> mobSpawnCommands) { this.mobSpawnCommands = mobSpawnCommands; }

    // Perms
    public String getPermission(){ return permission; }
    public void setPermission(@Nullable String permission){ this.permission = permission; }
    public String getPermissionMessage(){ return permissionMessage; }
    public void setPermissionMessage(@Nullable String permissionMessage){ this.permissionMessage = permissionMessage; }

    // Particle
    public Particle getSpawnParticleType() { return spawnParticleType; }
    public void setSpawnParticleType(@Nullable Particle spawnParticleType) { this.spawnParticleType = spawnParticleType; }
    public Location getSpawnParticleLocation() { return spawnParticleLocation; }
    public void setSpawnParticleLocation(@Nullable Location spawnParticleLocation) { this.spawnParticleLocation = spawnParticleLocation; }
    public String getParticleMessage() { return particleMessage; }

    public void setParticleMessage(String particleMessage) { this.particleMessage = particleMessage; }

}