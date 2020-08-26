package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundManager;
import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.queue.Queue;
import net.malfact.bgmanager.queue.QueueManager;
import net.malfact.bgmanager.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandQueue implements PluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a Player!");
            return true;
        }

        if (args.length < 1) {
            Battleground[] list = BattlegroundManager.getBattlegrounds();
            if (list.length > 0) {
                for (Battleground bg : list) {
                    sender.sendMessage(ChatColor.GOLD + ">> " + bg.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "There are no Battlegrounds!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")){
            Queue currentQueue = QueueManager.getPlayerQueue(player);
            if (currentQueue == null)
                player.sendMessage(ChatColor.RED + "You are not in a queue!");
            else {
                QueueManager.setPlayerQueue(player, null);
                player.sendMessage(Config.BG_QUEUE_LEAVE_COLOR + String.format(Config.BG_QUEUE_LEAVE, currentQueue.battleground.getName()));
            }
            return true;
        }

        String name = args[0];
        for (int i = 1; i < args.length; i++) {
            name = name + " " + args[i];
        }

        Battleground bg = BattlegroundManager.getBattleground(args[0], name);
        if (bg == null) {
            player.sendMessage(ChatColor.RED + name + " is not a Battleground!");
        } else {

            if (!bg.getEnabled()) {
                player.sendMessage(ChatColor.RED + name + " is not enabled!");
                return true;
            }

            Queue queue = QueueManager.getQueue(bg.getId());
            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Unable to join queue for " + bg.getName() + ". Queue is inactive!");
                return true;
            }

            QueueManager.setPlayerQueue(player, bg.getId());
            player.sendMessage(Config.BG_QUEUE_JOIN_COLOR + String.format(Config.BG_QUEUE_JOIN, queue.battleground.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabs = new ArrayList<>();
        if (args.length >= 1){
            ArrayList<String> options = new ArrayList<>();
            options.add("leave");
            for (Battleground bg : BattlegroundManager.getBattlegrounds()){
                if (bg.getEnabled()){
                    options.add(bg.getName());
                }
            }

            String name = args[0];
            for (int i = 1; i < args.length; i++) {
                name = name + " " + args[i];
            }

            StringUtil.copyPartialMatches(name, options, tabs);
        }

        Collections.sort(tabs);
        return tabs;
    }
}
