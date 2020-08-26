package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundManager;
import net.malfact.bgmanager.api.battleground.InstanceManager;
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

        if (args.length == 1){
            Battleground battleground = BattlegroundManager.getBattleground(args[0]);
            if (battleground == null)
                return true;

            BattlegroundInstance instance = battleground.createInstance();
            if (InstanceManager.get().registerInstance(battleground.getId(), instance)){
                player.sendMessage("Force created new Instance of " + battleground.getId());
            }
        }

        return true;
    }
}
