package net.malfact.bgmanager.api.battleground;

import org.bukkit.Color;

public enum TeamColor {
    DEFAULT(Color.YELLOW, org.bukkit.ChatColor.WHITE),
    RED(org.bukkit.Color.RED, org.bukkit.ChatColor.RED),
    BLUE(org.bukkit.Color.BLUE, org.bukkit.ChatColor.BLUE),
    GREEN(org.bukkit.Color.GREEN, org.bukkit.ChatColor.GREEN);

    public final org.bukkit.Color color;
    public final org.bukkit.ChatColor chatColor;

    TeamColor(org.bukkit.Color color, org.bukkit.ChatColor chatColor){
        this.color = color;
        this.chatColor = chatColor;
    }
}
