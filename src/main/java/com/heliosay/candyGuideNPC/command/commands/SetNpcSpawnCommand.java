package com.heliosay.candyGuideNPC.command.commands;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNpcSpawnCommand implements SubCommand {

    private final CandyGuideNPC plugin;

    public SetNpcSpawnCommand(CandyGuideNPC plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("guidenpc.setlocation")) {
            sender.sendMessage(ChatColor.RED + "[GuideNpc] Bu komutu kullanmak için yetkiniz yok!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[GuideNpc] Bu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        plugin.getConfig().set("npc.spawn-location.world", location.getWorld().getName());
        plugin.getConfig().set("npc.spawn-location.x", location.getX());
        plugin.getConfig().set("npc.spawn-location.y", location.getY());
        plugin.getConfig().set("npc.spawn-location.z", location.getZ());
        plugin.saveConfig();

        plugin.reloadNpcData();

        sender.sendMessage(ChatColor.GREEN + "[GuideNpc] NPC spawn konumu ayarlandı!");
        sender.sendMessage(ChatColor.GRAY + "Dünya: " + ChatColor.WHITE + location.getWorld().getName());
        sender.sendMessage(ChatColor.GRAY + "X: " + ChatColor.WHITE + String.format("%.2f", location.getX()));
        sender.sendMessage(ChatColor.GRAY + "Y: " + ChatColor.WHITE + String.format("%.2f", location.getY()));
        sender.sendMessage(ChatColor.GRAY + "Z: " + ChatColor.WHITE + String.format("%.2f", location.getZ()));

        return true;
    }
}