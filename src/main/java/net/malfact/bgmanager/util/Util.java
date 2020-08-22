package net.malfact.bgmanager.util;

import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.api.command.PluginCommand;
import net.malfact.bgmanager.queue.QueueManager;
import net.malfact.bgmanager.response.ResponseManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class Util {

    public static <T extends PluginCommand> T registerCommand(JavaPlugin plugin, String name, T commandExecutor){
        if (commandExecutor == null){
            plugin.getLogger().log(Level.SEVERE, "Unable to register command " + name);
            return null;
        }
        plugin.getCommand(name).setExecutor(commandExecutor);
        plugin.getCommand(name).setTabCompleter(commandExecutor);
        return commandExecutor;
    }

    public static void notifyPlayerBGReady(UUID uuid, String id){
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()){
            return;
        }

        TextComponent message01 = new TextComponent(Config.BG_READY_MESSAGE01);
        message01.setColor(Config.BG_READY_MESSAGE_COLOR);

        TextComponent message02 = new TextComponent(Config.BG_READY_MESSAGE02 + " ");
        message02.setColor(Config.BG_READY_MESSAGE_COLOR);

        SelectorComponent selectorYes = new SelectorComponent(Config.BG_READY_CONFIRM);
        selectorYes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bgmanager:response confirm"));
        selectorYes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Config.BG_READY_CONFIRM_HOVER)));
        selectorYes.setUnderlined(true);
        selectorYes.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        message02.addExtra(selectorYes);

        message02.addExtra(" or ");

        SelectorComponent selectorNo = new SelectorComponent(Config.BG_READY_DENY);
        selectorNo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bgmanager:response deny"));
        selectorNo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Config.BG_READY_DENY_HOVER)));
        selectorNo.setUnderlined(true);
        selectorNo.setColor(net.md_5.bungee.api.ChatColor.RED);
        message02.addExtra(selectorNo);

        player.spigot().sendMessage(message01);
        player.spigot().sendMessage(message02);
        player.playSound(player.getLocation(), Config.BG_READY_SOUND, Config.BG_READY_SOUND_VOLUME, Config.BG_READY_SOUND_PITCH);

        ResponseManager.get().registerResponseListener(player.getUniqueId(), QueueManager.get().getQueue(id));
    }

    public static Object parseString(String str){
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch(NumberFormatException e){ }
        try {
            double d = Double.parseDouble(str);
            return d;
        } catch(NumberFormatException e){ }
        try {
            float f = Float.parseFloat(str);
            return f;
        } catch(NumberFormatException e){ }


        boolean isABool = (str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false"));
        if (isABool)
            return Boolean.parseBoolean(str);
        else
            return str;


    }

    public static Method getEditCommand(Class clazz, String command){
        for (final Method method : clazz.getMethods()){
            if (method.isAnnotationPresent(EditCommand.class)){
                if (method.getAnnotation(EditCommand.class).cmd().equalsIgnoreCase(command)) {
                    return method;
                }
            }
        }
        return null;
    }
}
