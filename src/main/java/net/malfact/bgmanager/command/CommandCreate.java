package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.battleground.BattlegroundBase;
import net.malfact.bgmanager.BattlegroundManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandCreate implements PluginCommand {

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

        BattlegroundBase bg = new BattlegroundBase(args[0]);
        if (BattlegroundManager.registerBattleground(bg)) {
            sender.sendMessage(ChatColor.GOLD + "Created Battleground with ID<" + args[0] + ">");

            BattlegroundManager.saveBattleground(bg);
        } else
            sender.sendMessage(ChatColor.RED + "Battleground with ID<" + args[0] + "> already exists!");

        return true;
    }
}
