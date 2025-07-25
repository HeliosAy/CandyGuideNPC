package com.heliosay.candyGuideNPC.listener;

import com.heliosay.candyGuideNPC.npc.NpcConfig;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBlockerListener implements Listener {

    private final NpcConfig npcConfig;
    private List<String> allowedCommands;

    public CommandBlockerListener(NpcConfig npcConfig) {
        this.npcConfig = npcConfig;
        loadAllowedCommands();
    }

    private void loadAllowedCommands() {
        this.allowedCommands = npcConfig.getAllowedCommandsDuringGuide();
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String playerWorld = player.getWorld().getName();
        String command = event.getMessage().toLowerCase();

        if (player.isOp() || player.hasPermission("guidenpc.admin")) {
            return;
        }

        if (!player.hasPermission(npcConfig.getCompletedGuidePermission()) &&
                playerWorld.equals(npcConfig.getGuideStartTeleportLocation().getWorld().getName())) {

            String commandName = command.substring(1);

            for (String allowedCmd : allowedCommands) {
                if (commandName.equals(allowedCmd) || commandName.startsWith(allowedCmd + " ")) {
                    return;
                }
            }

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Rehberi tamamlamadan bu komutu kullanamazsın!");
        }
    }
}