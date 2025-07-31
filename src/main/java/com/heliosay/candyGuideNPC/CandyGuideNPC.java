package com.heliosay.candyGuideNPC;

import com.heliosay.candyGuideNPC.command.GuideNpcCommand;
import com.heliosay.candyGuideNPC.entity.PlayerMobManager;
import com.heliosay.candyGuideNPC.hologram.HologramManager;
import com.heliosay.candyGuideNPC.listener.*;
import com.heliosay.candyGuideNPC.music.MusicManager;
import com.heliosay.candyGuideNPC.npc.NpcConfig;
import com.heliosay.candyGuideNPC.npc.GuideManager;
import com.heliosay.candyGuideNPC.npc.NpcHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CandyGuideNPC extends JavaPlugin {
    private GuideManager guideManager;
    private NpcConfig npcConfig;
    private PlayerMobManager playerMobManager;
    private MusicManager musicManager;
    private NpcHandler npcHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HologramManager.setPlugin(this);
        this.npcConfig = new NpcConfig(this);
        this.playerMobManager = new PlayerMobManager(this);
        this.musicManager = new MusicManager(this);
        this.guideManager = new GuideManager(this, npcConfig, playerMobManager, musicManager);
        this.npcHandler = new NpcHandler(this, npcConfig);

        // Event listenerları kaydet
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(guideManager, npcConfig, musicManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(guideManager, musicManager), this);
        Bukkit.getPluginManager().registerEvents(new NpcInteractListener(guideManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerShiftListener(guideManager), this);
        Bukkit.getPluginManager().registerEvents(new MobTargetListener(this, playerMobManager), this);
        Bukkit.getPluginManager().registerEvents(new CommandBlockerListener(npcConfig), this);

        // Komutları Kaydet
        GuideNpcCommand guideNpcCommand = new GuideNpcCommand(this);
        getCommand("guidenpc").setExecutor(guideNpcCommand);
        getCommand("guidenpc").setTabCompleter(guideNpcCommand);

        // Bozuk NPCleri Temizle
        npcHandler.removeGhostNPCs(npcConfig.getNpcName());

        
        getLogger().info("GuideNPC Plugin successfully Enabled");
    }

    public void reloadNpcData() {
        reloadConfig();
        this.npcConfig.reload();

        if (guideManager != null) {
            guideManager.cleanup();
        }
        this.guideManager = new GuideManager(this, npcConfig, playerMobManager, musicManager);
        getLogger().info("Config file reloaded and GuideManager updated!");
    }

    @Override
    public void onDisable() {
        if (musicManager != null) { musicManager.cleanup(); }
        if (guideManager != null) { guideManager.cleanup(); }
        if (playerMobManager != null) { playerMobManager.cleanup(); }
        getLogger().info("GuideNPC Plugin has been disabled");
    }

    public GuideManager getGuideManager() {
        return guideManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public NpcConfig getNpcConfig() {
        return npcConfig;
    }

    public PlayerMobManager getPlayerMobManager() {
        return playerMobManager;
    }
}