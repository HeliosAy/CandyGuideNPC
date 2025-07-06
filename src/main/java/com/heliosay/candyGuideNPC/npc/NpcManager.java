package com.heliosay.candyGuideNPC.npc;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class NpcManager {
    private final JavaPlugin plugin;
    private final NPCRegistry registry;
    private final NpcConfig npcConfig;
    private final Map<UUID, NPC> playerNpcs = new HashMap<>();
    private final Map<UUID, Integer> playerProgress = new HashMap<>();
    private final Map<UUID, Long> lastInteraction = new HashMap<>();
    private final Set<UUID> playersWaitingForInput = new HashSet<>();

    public NpcManager(JavaPlugin plugin, NPCRegistry registry, NpcConfig npcConfig) {
        this.plugin = plugin;
        this.registry = registry;
        this.npcConfig = npcConfig;
    }

}
