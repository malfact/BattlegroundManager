package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.BattlegroundManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDelete implements PluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.RED + command.getUsage());
            return true;
        } else if (args.length > 1){
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            sender.sendMessage(ChatColor.RED + command.getUsage());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Deleted Battleground with ID<" + args[0] + ">");
        BattlegroundManager.unregisterBattleground(args[0]);
        BattlegroundManager.deleteBattleground(args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabs = new ArrayList<>();
        if (args.length == 1){
            ArrayList<String> battlegrounds = new ArrayList<>(Arrays.asList(BattlegroundManager.getBattlegroundIds()));
            StringUtil.copyPartialMatches(args[0], battlegrounds, tabs);
        }

        Collections.sort(tabs);
        return tabs;
    }
}
