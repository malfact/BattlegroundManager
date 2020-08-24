package net.malfact.bgmanager.command;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.Team;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.api.battleground.BattlegroundManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandSetTeamSpawn implements PluginCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player)
            player = (Player) sender;

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Command can only be used by a Player!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.RED + command.getUsage());
            return true;
        } else if (args.length > 2){
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            sender.sendMessage(ChatColor.RED + command.getUsage());
            return true;
        }

        Battleground bg = BattlegroundManager.getBattleground(args[0]);
        if (bg == null) {
            player.sendMessage(ChatColor.RED + args[0] + " is not a Battleground!");
            return true;
        }

        Team team = bg.getTeam(TeamColor.valueOf(args[1]));
        if (team == null){
            player.sendMessage(ChatColor.RED + args[1] + " is not a valid team!");
            return true;
        }

        team.setSpawnLocation(player.getLocation());
        player.sendMessage(ChatColor.GOLD + "Set " + team.getColor() + " Team Spawn in " + args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabs = new ArrayList<>();
        if (args.length == 1){
            ArrayList<String> battlegrounds = new ArrayList<>(Arrays.asList(BattlegroundManager.getBattlegroundIds()));
            StringUtil.copyPartialMatches(args[0], battlegrounds, tabs);

        } else if (args.length == 2){
            TeamColor[] teamColors = TeamColor.values();
            ArrayList<String> teams = new ArrayList<>();
            for (TeamColor color : teamColors)
                teams.add(color.toString());

            StringUtil.copyPartialMatches(args[1], teams, tabs);
        }

        Collections.sort(tabs);
        return tabs;
    }
}
