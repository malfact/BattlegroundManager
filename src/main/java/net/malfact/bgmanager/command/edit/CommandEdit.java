package net.malfact.bgmanager.command.edit;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.api.command.SubCommand;
import net.malfact.bgmanager.api.command.SubCommandContainer;
import net.malfact.bgmanager.BattlegroundManager;
import net.malfact.bgmanager.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandEdit implements PluginCommand, SubCommandContainer {

    private final HashMap<String, SubCommand> subCommands = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player)
            player = (Player) sender;
        else
            return true;

        Battleground battleground = BattlegroundManager.getBattleground(args[0]);
        if (battleground == null){
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid battleground!");
            return true;
        }

        if (args.length == 1) {
            player.teleport(battleground.getWorld().getSpawnLocation());
            player.setGameMode(GameMode.CREATIVE);

        } else if (args.length >= 2){
            if (args[1].substring(0,1).equalsIgnoreCase("#")){
                Method m = Util.getEditCommand(battleground.getClass(), args[1].substring(1));

                String[] passArgs = new String[args.length-2];
                for (int i = 2; i < args.length; i++){
                    passArgs[i-2] = args[i];
                }

                try {
                    m.invoke(battleground, player, passArgs);
                } catch (IllegalAccessException| InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                SubCommand cmd = subCommands.get(args[1]);
                if (cmd == null){
                    sender.sendMessage(ChatColor.RED + "Invalid Sub-command! (" + args[1] + ")");
                    sender.sendMessage(ChatColor.RED + command.getUsage());
                    return true;
                }

                String[] newArgs = new String[args.length - 1];
                newArgs[0] = args[0];
                for (int i = 2; i < args.length; i++){
                    newArgs[i-1] = args[i];
                }

                return cmd.onCommand(sender, command, label, newArgs, args);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            sender.sendMessage(ChatColor.RED + command.getUsage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabs = new ArrayList<>();
        if (args.length == 1){
            ArrayList<String> battlegrounds = new ArrayList<>(
                    Arrays.asList(BattlegroundManager.getBattlegroundIds()));
            StringUtil.copyPartialMatches(args[0], battlegrounds, tabs);

        } else if (args.length == 2){
            ArrayList<String> commands = new ArrayList<>();

            //Get edit commands of battleground
            Battleground battleground = BattlegroundManager.getBattleground(args[0]);
            if (battleground != null) {
                for (final Method method : battleground.getClass().getMethods()) {
                    if (method.isAnnotationPresent(EditCommand.class)) {
                        commands.add("#" + method.getAnnotation(EditCommand.class).cmd());
                    }
                }
            }

            // Get sub commands
            commands.addAll(subCommands.keySet());

            StringUtil.copyPartialMatches(args[1], commands, tabs);
        } else if (args.length >= 3){
            if (subCommands.containsKey(args[1])){
                String[] newArgs = new String[args.length - 1];
                newArgs[0] = args[0];
                for (int i = 2; i < args.length; i++){
                    newArgs[i-1] = args[i];
                }

                return subCommands.get(args[1]).onTabComplete(sender, command, alias, newArgs);
            }
        }

        Collections.sort(tabs);
        return tabs;
    }

    @Override
    public SubCommandContainer registerSubCommand(String name, SubCommand executor) {
        subCommands.put(name, executor);
        return this;
    }
}
