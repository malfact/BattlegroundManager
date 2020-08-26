package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.command.PluginCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTest implements PluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a Player!");
            return true;
        }



        return true;
    }
}
