package net.malfact.bgmanager;

import net.malfact.bgmanager.api.battleground.BattlegroundManager;
import net.malfact.bgmanager.api.file.FileDirectory;
import net.malfact.bgmanager.command.*;
import net.malfact.bgmanager.command.edit.CommandEdit;
import net.malfact.bgmanager.command.edit.EditCommandDoodad;
import net.malfact.bgmanager.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BgManager extends JavaPlugin {

    private static BgManager instance;

    public static BgManager getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        FileDirectory.WORLD_INSTANCE.clearDirectory();

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

        BattlegroundManager.loadBattlegrounds();

        registerListener(new Listener() {
            @EventHandler
            public void onPlayerEditBook(PlayerEditBookEvent event){
                if (event.isSigning()){
                    event.getPlayer().sendMessage("SIGNING");
                    event.setCancelled(true);
                }

                for (String s : event.getNewBookMeta().getPages()) {
                    event.getPlayer().sendMessage(s.replace("\n", ""));
                }
            }
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        BattlegroundManager.unloadBattlegrounds();
        BattlegroundManager.saveBattlegrounds();
    }

    public static void registerListener(Listener listener){
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void unregisterListener(Listener listener){
        HandlerList.unregisterAll(listener);
    }
}
