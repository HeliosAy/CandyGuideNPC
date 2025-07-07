package com.heliosay.candyGuideNPC;

import com.heliosay.candyGuideNPC.command.GuideNpcCommand;
import com.heliosay.candyGuideNPC.listener.NpcInteractListener;
import com.heliosay.candyGuideNPC.listener.PlayerJoinListener;
import com.heliosay.candyGuideNPC.listener.PlayerQuitListener;
import com.heliosay.candyGuideNPC.listener.PlayerShiftListener;
import com.heliosay.candyGuideNPC.npc.NpcConfig;
import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CandyGuideNPC extends JavaPlugin {
    private NpcManager npcManager;
    private NpcConfig npcConfig;

    @Override
    public void onEnable() {

        this.npcConfig = new NpcConfig(this);
        this.npcManager = new NpcManager(this, npcConfig);

        // Event listenerları kaydet
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(npcManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(npcManager), this);
        Bukkit.getPluginManager().registerEvents(new NpcInteractListener(npcManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerShiftListener(npcManager), this);

        // Komutları Kaydet
        getCommand("guidenpc").setExecutor(new GuideNpcCommand(this));

        getLogger().info("GuideNPC Plugin başarıyla yüklendi!");

    }

    public void reloadNpcData() {
        reloadConfig();
        this.npcConfig.reload();
        getLogger().info("Config dosyası yeniden yüklendi!");
    }


    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.cleanup();
        }
        getLogger().info("GuideNPC Plugin kapatıldı ve tüm NPC'ler temizlendi!");
    }

}
