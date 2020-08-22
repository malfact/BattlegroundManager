package net.malfact.bgmanager.battleground;

public enum TeamColor {
    RED(org.bukkit.Color.RED, org.bukkit.ChatColor.RED),
    BLUE(org.bukkit.Color.BLUE, org.bukkit.ChatColor.BLUE);

    public final org.bukkit.Color color;
    public final org.bukkit.ChatColor chatColor;

    TeamColor(org.bukkit.Color color, org.bukkit.ChatColor chatColor){
        this.color = color;
        this.chatColor = chatColor;
    }

    TeamColor(org.bukkit.Color color){
        this(color, org.bukkit.ChatColor.WHITE);
    }
}
