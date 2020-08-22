package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.BattlegroundManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandList implements PluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Battleground[] list = BattlegroundManager.get().getBattlegrounds();
        if (list.length > 0) {
            for (Battleground bg : list) {
                sender.sendMessage(bg.getName()+ " - <" + bg.getId() + "> ("
                        + (bg.getEnabled() ? "Enabled" : "Disabled") + ")");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There are no Battlegrounds!");
        }

        return true;
    }

}
