package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.response.ResponseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandResponse implements PluginCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a Player!");
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")){
            ResponseManager.get().sendResponse(player.getUniqueId(), true);
        } else if (args[0].equalsIgnoreCase("deny")){
            ResponseManager.get().sendResponse(player.getUniqueId(), false);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid response!");
        }

        return true;
    }
}
