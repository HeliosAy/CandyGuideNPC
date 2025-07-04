package com.heliosay.candyGuideNPC;

import org.bukkit.plugin.java.JavaPlugin;

public final class CandyGuideNPC extends JavaPlugin {

    NpcManager npcManager = new NpcManager();

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("Plugin kapatıldı");
    }
}
