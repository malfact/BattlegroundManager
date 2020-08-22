package net.malfact.bgmanager.api.command;

import net.malfact.bgmanager.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public interface SubCommand extends TabCompleter {
    boolean onCommand(CommandSender sender, Command command, String label, String[] args, String[] preArgs);

    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
