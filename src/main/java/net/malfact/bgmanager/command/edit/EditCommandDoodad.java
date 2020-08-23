package net.malfact.bgmanager.command.edit;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.command.SubCommand;
import net.malfact.bgmanager.BattlegroundManager;
import net.malfact.bgmanager.api.doodad.DoodadPhysical;
import net.malfact.bgmanager.api.doodad.DoodadType;
import net.malfact.bgmanager.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditCommandDoodad implements SubCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args, String[] preArgs) {
        Battleground battleground = BattlegroundManager.getBattleground(args[0]);
        Player player = (Player) sender;

        if (args.length == 1){
            battleground.getDoodads().forEach(doodad -> {
                sender.sendMessage(ChatColor.YELLOW + ">> (" + DoodadType.getByClass(doodad.getClass()).getName() + ")"
                        + doodad.getId());
            });
        } else if (args.length >= 3) {
            switch(args[1].toLowerCase()){
                case "#create":
                    if (player.getWorld() != battleground.getWorld()){
                        player.sendMessage(ChatColor.RED + "You are not in the correct world!");
                        return true;
                    }

                    if (args.length == 3)
                        sender.sendMessage(ChatColor.RED + "ID is required when creating a doodad!");
                    else if (args.length == 4) {
                        DoodadType doodadType = DoodadType.getByName(args[2]);
                        if (doodadType == null){
                            sender.sendMessage(ChatColor.RED + "Invalid Doodad Type! " + args[2]);
                            return true;
                        }

                        if (args[3].substring(0,1).equalsIgnoreCase("#")){
                            sender.sendMessage(ChatColor.RED + "Invalid ID! ID's cannot begin with '#'!");
                            return true;
                        }

                        Doodad newDoodad = battleground.addDoodad(args[3], doodadType);
                        if (newDoodad != null) {
                            sender.sendMessage(ChatColor.GOLD + "Created Doodad of Type <" + args[2] + "> in <"
                                    + args[0] + "> with id of <" + args[3] + ">");
                            if (newDoodad instanceof DoodadPhysical){
                                ((DoodadPhysical) newDoodad).setLocation(player.getLocation());
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Unable to create Doodad with id <" + args[3] + ">");
                        }
                    }

                    return true;
                case "#delete":
                    if (battleground.getDoodad(args[2]) == null){
                        sender.sendMessage(ChatColor.RED + args[2] + " is not a valid doodad of " + args[0]);
                        return true;
                    }

                    battleground.removeDoodad(args[2]);
                    sender.sendMessage(ChatColor.GOLD + "Doodad <" + args[2] + "> removed from <" + args[0] + ">");
                    break;
                default:
                    Doodad doodad = battleground.getDoodad(args[1]);
                    if (doodad == null){
                        sender.sendMessage(ChatColor.RED + args[1] + " is not a valid doodad of " + args[0]);
                        return true;
                    }

                    Method m = Util.getEditCommand(battleground.getDoodad(args[1]).getClass(), args[2].substring(1));

                    String[] passArgs = new String[args.length-3];
                    for (int i = 3; i < args.length; i++){
                        passArgs[i-3] = args[i];
                    }

                    try {
                        m.invoke(doodad, player, passArgs);
                    } catch (IllegalAccessException|InvocationTargetException e) {
                        e.printStackTrace();
                    }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabs = new ArrayList<>();

        Battleground battleground = BattlegroundManager.getBattleground(args[0]);

        if (args.length == 2){
            ArrayList<String> doodads = new ArrayList<>();

            doodads.add("#create");
            doodads.add("#delete");

            battleground.getDoodads().forEach(doodad -> doodads.add(doodad.getId()));
            StringUtil.copyPartialMatches(args[1], doodads, tabs);
        } else if (args.length == 3){
            if (args[1].equalsIgnoreCase("#create")){
                ArrayList<String> doodadTypes = new ArrayList<>();
                Arrays.asList(DoodadType.values()).forEach(type -> doodadTypes.add(type.getName()));
                StringUtil.copyPartialMatches(args[2], doodadTypes, tabs);
            } else if (args[1].equalsIgnoreCase("#delete")){
                ArrayList<String> doodads = new ArrayList<>();
                battleground.getDoodads().forEach(doodad -> doodads.add(doodad.getId()));
                StringUtil.copyPartialMatches(args[2], doodads, tabs);
            } else {
                Doodad doodad = battleground.getDoodad(args[1]);

                if (doodad != null) {
                    ArrayList<String> commands = new ArrayList<>();
                    for (final Method method : doodad.getClass().getMethods()) {
                        if (method.isAnnotationPresent(EditCommand.class)) {
                            commands.add("#" + method.getAnnotation(EditCommand.class).cmd());
                        }
                    }

                    StringUtil.copyPartialMatches(args[2], commands, tabs);
                }
            }
        }
//        else if (args.length == 4){
//            if (args[1].substring(0,1).equalsIgnoreCase("#")) {
//                Method m = Util.getEditCommand(battleground.getDoodad(args[1]).getClass(), args[2].substring(1));
//                if (m != null) {
//                    Class<?>[] classes = m.getAnnotation(EditCommand.class).args();
//                    if (classes[0].isEnum()){
//                        Object[] types = classes[0].getEnumConstants();
//                    }
//                }
//            }
//        }

        Collections.sort(tabs);
        return tabs;
    }
}
