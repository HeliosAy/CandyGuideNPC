package com.heliosay.candyGuideNPC.npc;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.PlayerFilter;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.heliosay.candyGuideNPC.util.ChatHelper.colorize;

public class NpcHandler {

    private final CandyGuideNPC plugin;
    private final NPCRegistry registry;
    private final NpcConfig npcConfig;
    private final Map<UUID, NPC> playerNpcs;
    private final Map<UUID, BukkitRunnable> activeNavigationTasks;

    public NpcHandler(CandyGuideNPC plugin, NpcConfig npcConfig) {
        this.plugin = plugin;
        this.registry = CitizensAPI.getNPCRegistry();
        this.npcConfig = npcConfig;
        this.playerNpcs = new ConcurrentHashMap<>();
        this.activeNavigationTasks = new ConcurrentHashMap<>();
    }

    public void spawnNpc(Player player) {
        if (!npcConfig.isEnabled()) return;
        UUID uuid = player.getUniqueId();
        if (playerNpcs.containsKey(uuid)) {
            removeNpc(uuid);
        }

        Location spawnLoc = npcConfig.getSpawnLocation();
        if (spawnLoc == null || spawnLoc.getWorld() == null) {
            player.sendMessage("§cInvalid NPC spawn location!");
            return;
        }

        NPC npc = registry.createNPC(npcConfig.getNpcType(),
                colorize(npcConfig.getNpcName()));
        npc.spawn(spawnLoc);

        npc.data().set(NPC.Metadata.GLOWING, npcConfig.isGlowing());
        npc.data().setPersistent(NPC.Metadata.SILENT, true);

        // NPC tipi PLAYER ise skini ayarla
        if (npcConfig.getNpcType() == EntityType.PLAYER) {
            String skinName = npcConfig.getNpcSkin();
            if (skinName != null && !skinName.isEmpty()) {
                SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinName(skinName, true);
                plugin.getLogger().info("Skin applied to NPC: " + skinName);
            } else {
                SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
                skinTrait.setSkinName("", true);
                plugin.getLogger().warning("NPC type is PLAYER but 'npc-skin' in config is empty or not set. Default skin will be used.");
            }
        }

        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.lookClose(true);
        lookClose.setRange(npcConfig.getLookRange());
        lookClose.setRealisticLooking(true);

        PlayerFilter filter = npc.getOrAddTrait(PlayerFilter.class);
        filter.clear();
        filter.setAllowlist();
        filter.addPlayer(uuid);
        filter.recalculate();

        playerNpcs.put(uuid, npc);
    }

    public void removeNpc(UUID uuid) {
        BukkitRunnable navTask = activeNavigationTasks.remove(uuid);
        if (navTask != null) {
            navTask.cancel();
        }

        if (playerNpcs.containsKey(uuid)) {
            NPC npc = playerNpcs.remove(uuid);
            if (npc.isSpawned()) {
                npc.despawn();
            }
            npc.destroy();
        }
    }

    public NPC getNpc(UUID playerUUID) {
        return playerNpcs.get(playerUUID);
    }

    public boolean isNpcSpawned(UUID playerUUID) {
        NPC npc = playerNpcs.get(playerUUID);
        return npc != null && npc.isSpawned();
    }

    public void startNpcNavigation(Player player, NPC npc, Location target, Runnable onArrival) {
        BukkitRunnable oldTask = activeNavigationTasks.remove(player.getUniqueId());
        if (oldTask != null) {
            oldTask.cancel();
        }

        npc.getNavigator().setTarget(target);

        BukkitRunnable navigationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    activeNavigationTasks.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                if (!npc.getNavigator().isNavigating() || npc.getStoredLocation().distance(target) < 1.0) {
                    onArrival.run();
                    cancel();
                    activeNavigationTasks.remove(player.getUniqueId());
                }
            }
        };
        activeNavigationTasks.put(player.getUniqueId(), navigationTask);
        navigationTask.runTaskTimer(plugin, 10L, 10L);
    }

    public void cleanup() {
        for (BukkitRunnable task : activeNavigationTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        activeNavigationTasks.clear();

        Set<UUID> uuidsToDespawn = new HashSet<>(playerNpcs.keySet());
        for (UUID uuid : uuidsToDespawn) {
            removeNpc(uuid);
        }
        playerNpcs.clear();
        plugin.getLogger().info("All active Citizens NPCs and navigation tasks in NpcHandler have been cleaned up.");
    }

    public void removeGhostNPCs(String targetName) {
        int removeCount = 0;

        for (NPC npc : registry) {
            if (ChatColor.stripColor(npc.getName()).equalsIgnoreCase(ChatColor.stripColor(targetName))) {
                npc.destroy();
                removeCount++;
            }
        }

        plugin.getLogger().info("During server startup: (" + removeCount + " ghost NPC(s) removed)");
    }
}
