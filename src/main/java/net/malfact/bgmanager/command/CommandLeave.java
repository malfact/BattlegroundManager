package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.battleground.InstanceManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.command.PluginCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave implements PluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a Player!");
            return true;
        }

        BattlegroundInstance instance = InstanceManager.get().getPlayerInstance(player);

        if (instance == null){
            player.sendMessage(ChatColor.RED + "You are not in a battleground!");
        } else {
            player.sendMessage(ChatColor.RED + "You have left the battleground!");
            instance.removePlayer(player);
        }

        return true;
    }
}
