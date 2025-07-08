package com.heliosay.candyGuideNPC.npc;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuideStep {

    public enum StepType{
        MESSAGE,
        WAYPOINT,
        COMMAND
    }
    private StepType type;
    private List<String> messages;
    private List<String> commands;
    private Location location;
    private String permission;
    private String permissionMessage;

    //Getter ve settrlar
    public StepType getType() {return type;}
    public void setType(StepType type) {this.type = type;}

    public List<String> getMessages() {return messages;}
    public void setMessages(List<String> messages) {this.messages = messages;}

    public List<String> getCommands() {return commands;}
    public void setCommands(List<String> commands) {this.commands = commands;}

    public Location getLocation() {return location;}
    public void setLocation(Location location) {this.location = location;}

    public String getPermission(){ return permission; }
    public void setPermission(@Nullable String permission){ this.permission = permission; }

    public String getPermissionMessage(){ return permissionMessage; }
    public void setPermissionMessage(@Nullable String permissionMessage){ this.permissionMessage = permissionMessage; }

}
