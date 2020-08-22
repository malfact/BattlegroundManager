package net.malfact.bgmanager.command;

import net.malfact.bgmanager.WorldManager;
import net.malfact.bgmanager.api.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        if (player.getInventory().getItemInMainHand() != null){
            player.sendMessage(player.getInventory().getItemInMainHand().toString());
        }

        //BattlegroundManager.get().saveBattlegrounds();

        return true;
    }
}