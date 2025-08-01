package com.heliosay.candyGuideNPC.command;

import com.heliosay.candyGuideNPC.CandyGuideNPC;
import com.heliosay.candyGuideNPC.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GuideNpcCommand implements CommandExecutor, TabCompleter {

    private final CandyGuideNPC plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public GuideNpcCommand(CandyGuideNPC plugin) {
        this.plugin = plugin;

        // Alt komutlar register ediliyor
        subCommands.put("reload", new ReloadCommand(plugin));
        subCommands.put("setstartlocation", new SetStartLocationCommand(plugin));
        subCommands.put("setcompletedlocation", new SetCompletedLocationCommand(plugin));
        subCommands.put("setnpcspawn", new SetNpcSpawnCommand(plugin));

        // Kısa versiyonlar
        subCommands.put("setstart", new SetStartLocationCommand(plugin));
        subCommands.put("setcompleted", new SetCompletedLocationCommand(plugin));
        subCommands.put("setnpc", new SetNpcSpawnCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§6--- GuideNPC Commands ---");
            sender.sendMessage("§e/guidenpc reload §7- Reloads the config file");
            sender.sendMessage("§e/guidenpc setstartlocation §7- Sets the start teleport location");
            sender.sendMessage("§e/guidenpc setcompletedlocation §7- Sets the completion teleport location");
            sender.sendMessage("§e/guidenpc setnpcspawn §7- Sets the NPC spawn location");
            sender.sendMessage("§7Short aliases: §esetstart, setcompleted, setnpc");
            return true;
        }

        String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(sub);

        if (subCommand != null) {
            return subCommand.execute(sender, args);
        } else {
            sender.sendMessage("§cUnknown command: §f" + sub);
            sender.sendMessage("§7Available commands: reload, setstartlocation, setcompletedlocation, setnpcspawn");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}