package com.heliosay.candyGuideNPC.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NpcConfig {

    private final JavaPlugin plugin;
    private FileConfiguration config;


    public NpcConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }
    // PLUGIN RELOAD
    public void reload(){
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    // NPC AKTIF MI
    public boolean isEnabled(){
        return config.getBoolean("npc.enabled");
    }

    // NPC ADI
    public String getNpcName(){
        return config.getString("npc.name");
    }

    // NPC TÜRÜ
    public EntityType getNpcType(){
        try {
            return EntityType.valueOf(config.getString("npc.type","villager").toUpperCase());
        }catch (Exception e){
            return EntityType.VILLAGER;
        }
    }

    // NPC PARLASIN MI
    public boolean isGlowing(){
        return config.getBoolean("npc.glowing");
    }

    // NPC OYUNCUYA BAKMA MESAFESI
    public int getlookRange(){
        return config.getInt("npc.lookrange");
    }

    // Npc etkileşim Cooldown
    public long getCooldown(){
        return config.getLong("npc.cooldown",1000);
    }

    // Npc etkileşim ses efekti olsun mu
    public boolean isSoundEnabled(){
        return config.getBoolean("npc.sound-enabled",true);
    }

    // Adımlar arası boş satır
    public int getEmptyLines(){
        return config.getInt("chat.empty-lines",15);
    }

    // Npc etkileşim sonrası devam etme mesajı
    public String getContinueMessage(){
        return config.getString("chat.continue-message","&7Devam etmek için &eShift &7tuşuna basın veya &eNPC'ye &7tıklayın");
    }

    public Location getSpawnLocation(){
        String world = config.getString("npc.spawn-location.world","world");
        double x = config.getDouble("npc.spawn-location.x",100);
        double y = config.getDouble("npc.spawn-location.y",64);
        double z = config.getDouble("npc.spawn-location.z",100);
        return new Location(Bukkit.getWorld(world),x,y,z);

    }

    // Oyuncu oyuna girince gonderilcek mesaj
    public List<String> getWelcomeMessages() {
        return config.getStringList("messages.welcome");
    }

    // Npc Adımları bitince gonderilcek mesaj
    public List<String> getFinishMessages(){
        return config.getStringList("messages.finish");
    }

    // Npc adımları
    public List<GuideStep> getGuideSteps(){
        List<GuideStep> steps = new ArrayList<>();
        ConfigurationSection stepsSection = config.getConfigurationSection("guide-steps");
        if (stepsSection==null) return steps;
        for (String key:stepsSection.getKeys(false)){
            ConfigurationSection stepSection = stepsSection.getConfigurationSection(key);
            if (stepSection==null) continue;

            GuideStep step = new GuideStep();
            String typeStr = stepSection.getString("type","MESSAGE");

            try{
                step.setType(GuideStep.StepType.valueOf(typeStr.toUpperCase()));
            }catch (Exception e){
                step.setType(GuideStep.StepType.MESSAGE);
            }

            step.setMessages(stepsSection.getStringList("messages"));
            step.setCommands(stepSection.getStringList("commands"));

            // WAYPOINT ICIN KONUM
            if (step.getType() == GuideStep.StepType.WAYPOINT){
                String world = stepSection.getString("location.world","world");
                double x = stepSection.getDouble("location.x",100);
                double y = stepSection.getDouble("location.y",64);
                double z = stepSection.getDouble("location.z",100);
                step.setLocation((new Location(Bukkit.getWorld(world),x,y,z)));
            }
            steps.add(step);

        }
        return steps;
    }
    
}
