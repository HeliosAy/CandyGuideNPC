package com.heliosay.candyGuideNPC.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatHelper {

    private final int emptyLines;

    public ChatHelper(int emptyLines) {
        this.emptyLines = emptyLines;
    }

    public void clearChat(Player player) {
        for (int i = 0; i < emptyLines; i++) {
            player.sendMessage("");
        }
    }

    public String centerMessage(String message) {
        int chatWidth = 55;
        String strippedMessage = ChatColor.stripColor(message);
        int messageLength = strippedMessage.length();

        if (messageLength >= chatWidth) return message;

        int spaces = (chatWidth - messageLength) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
        sb.append(message);
        return sb.toString();
    }
}