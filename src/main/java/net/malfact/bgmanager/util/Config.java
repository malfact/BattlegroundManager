package net.malfact.bgmanager.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;

public class Config {

    public static final String BG_READY_MESSAGE01 = "A Battleground is ready!";
    public static final String BG_READY_MESSAGE02 = "Do you wish to join?";
    public static final String BG_READY_CONFIRM = "Yes";
    public static final String BG_READY_CONFIRM_HOVER = "Yes, I would like to join!";
    public static final String BG_READY_DENY = "No";
    public static final String BG_READY_DENY_HOVER = "No, I would not like to join!";

    // %s is replaced with Battleground name
    public static final String BG_QUEUE_JOIN = ">> You have joined the Queue for %s <<";
    public static final String BG_QUEUE_LEAVE = ">> You have left the Queue for %s <<";

    // 1: BG Id 2: New BG Name 3: CMD Color 4: CMD Highlight Color
    public static final String BG_CMD_SETNAME = "%3$sBattleground <%4$s%1$s%3$s> renamed to \"%4$s%2$s%3$s\"";
    // 1: BG Id 2: Enabled/Disabled 3: CMD Color 4: CMD Highlight Color 5: Enabled/Disabled Color
    public static final String BG_CMD_SETENABLED = "%3$sBattleground <%4$s%1$s%3$s> is now <%5$s%2$s%3$s>";
    // 1: BG Id 2: Value 3: CMD Color 4: CMD Highlight color
    public static final String BG_CMD_SETMAXPLAYERS = "%3$sBattleground <%4$s%1$s%3$s> max players set to <%4$s%2$s%3$s>";
    // 1: BG Id 2: Value 3: CMD Color 4: CMD Highlight color
    public static final String BG_CMD_SETMINPLAYERS = "%3$sBattleground <%4$s%1$s%3$s> min players set to <%4$s%2$s%3$s>";

    public static final Sound BG_READY_SOUND = Sound.BLOCK_BELL_USE;
    public static final float BG_READY_SOUND_VOLUME = 3.0F;
    public static final float BG_READY_SOUND_PITCH = 0.5F;

    public static final ChatColor BG_READY_MESSAGE_COLOR = ChatColor.GOLD;

    public static final ChatColor BG_QUEUE_JOIN_COLOR = ChatColor.GOLD;
    public static final ChatColor BG_QUEUE_LEAVE_COLOR = ChatColor.RED;

    public static final ChatColor COMMAND_COLOR = ChatColor.GOLD;
    public static final ChatColor COMMAND_HIGHLIGHT = ChatColor.AQUA;
}
