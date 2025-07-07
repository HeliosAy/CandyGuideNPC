package com.heliosay.candyGuideNPC.command;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    boolean execute(CommandSender sender, String[] args);
}
