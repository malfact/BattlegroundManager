package net.malfact.bgmanager;

import net.malfact.bgmanager.command.*;
import net.malfact.bgmanager.command.edit.CommandEdit;
import net.malfact.bgmanager.command.edit.EditCommandDoodad;
import net.malfact.bgmanager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BgManager extends JavaPlugin {

    private static BgManager instance;

    public static BgManager getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Util.registerCommand(this, "queue", new CommandQueue());
        Util.registerCommand(this, "create", new CommandCreate());
        Util.registerCommand(this, "delete", new CommandDelete());
        Util.registerCommand(this, "bglist", new CommandList());
        Util.registerCommand(this, "edit", new CommandEdit())
                .registerSubCommand("doodad", new EditCommandDoodad());
        Util.registerCommand(this, "setteamspawn", new CommandSetTeamSpawn());
        Util.registerCommand(this, "response", new CommandResponse());
        Util.registerCommand(this, "leave", new CommandLeave());
        Util.registerCommand(this, "test", new CommandTest());


        BattlegroundManager.get().loadBattlegrounds();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        BattlegroundManager.get().unloadBattlegrounds();
        BattlegroundManager.get().saveBattlegrounds();
    }

    public static void registerListener(Listener listener){
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void unregisterListener(Listener listener){
        HandlerList.unregisterAll(listener);
    }
}
