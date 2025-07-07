package com.heliosay.candyGuideNPC.command;

import com.heliosay.candyGuideNPC.command.commands.ReloadCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GuideNpcCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public GuideNpcCommand(JavaPlugin plugin) {
        this.plugin = plugin;

        // Alt komutlar register ediliyor
        subCommands.put("reload", new ReloadCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§6--- GuideNPC Komutları ---");
            sender.sendMessage("§e/guidenpc reload - Config dosyasını yeniden yükler");
            return true;
        }

        String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(sub);

        if (subCommand != null) {
            return subCommand.execute(sender, args);
        } else {
            sender.sendMessage("§cBilinmeyen komut: §f" + sub);
            return true;
        }
    }
}
