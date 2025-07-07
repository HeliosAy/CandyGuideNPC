package com.heliosay.candyGuideNPC.command.commands;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.command.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

    private final CandyGuideNPC plugin;

    public ReloadCommand(CandyGuideNPC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("guidenpc.reload")) {
            sender.sendMessage(ChatColor.RED + "[GuideNpc] Bu komutu kullanmak için yetkiniz yok");
            return true;
        }

        plugin.reloadNpcData();
        sender.sendMessage(ChatColor.GREEN + "[GuideNpc] Config dosyası yeniden yüklendi!");
        return true;
    }
}
