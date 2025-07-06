package com.heliosay.candyGuideNPC;

import com.heliosay.candyGuideNPC.npc.NpcConfig;
import com.heliosay.candyGuideNPC.npc.NpcManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CandyGuideNPC extends JavaPlugin {
    private NpcManager npcManager;
    private NpcConfig npcConfig;

    @Override
    public void onEnable() {

        this.npcConfig = new NpcConfig(this);
        this.npcManager = new NpcManager(this, npcConfig);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("Plugin kapatıldı");
    }
}
