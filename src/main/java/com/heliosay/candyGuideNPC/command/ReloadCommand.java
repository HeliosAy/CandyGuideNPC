package com.heliosay.candyGuideNPC.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length==1 && args[0].equalsIgnoreCase("reload")){
            if (sender.hasPermission("guidenpc.reload")){
                // TODO: RELOAD METHOD EKLENİCEK
                sender.sendMessage(ChatColor.GREEN + "[GuideNpc] Dosyalar yeniden yüklendi");
                return true;
            }else {
                sender.sendMessage(ChatColor.RED + "[GuideNpc] Bu komutu kullanmak için yetkiniz yok");
                return true;
            }
        }
        return true;
    }
}
