package com.heliosay.candyGuideNPC.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GuideNpcCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(org.bukkit.ChatColor.GOLD + "=== GuideNPC Komutları ===");
            sender.sendMessage(ChatColor.YELLOW + "/guidenpc reload - Config dosyasını yeniden yükler");
            return true;
        }
        return true;
    }
}
