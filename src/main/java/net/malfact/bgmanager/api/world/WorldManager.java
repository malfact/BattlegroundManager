package net.malfact.bgmanager.api.world;

import net.malfact.bgmanager.api.FileUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;

public final class WorldManager {

    @Deprecated
    public static File getBaseDirectory(){
        return WorldDirectory.BASE.getDirectory();
    }

    public static World loadWorld(WorldDirectory directory, String name){
        WorldCreator worldCreator = new WorldCreator(directory.getDirectory() + "/" + name);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.FLAT);

        World world = worldCreator.createWorld();
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);

        return world;
    }

    public static boolean unloadWorld(WorldDirectory directory, String name, boolean save){
        World world = getWorld(directory, name);
        if (world == null)
            return true;

        for (Player player : world.getPlayers())
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        return Bukkit.unloadWorld(world, save);
    }

    public static World getWorld(WorldDirectory directory, String name){
        return Bukkit.getWorld(directory.getDirectory() + "/" + name);
    }

    public static World copyWorld(WorldDirectory sourceDirectory, String sourceName, WorldDirectory targetDirectory, String targetName){
        FileUtil.copy(new File(sourceDirectory.getDirectory(),sourceName), new File(targetDirectory.getDirectory(), targetName));
        return loadWorld(targetDirectory, targetName);
    }

    public static boolean deleteWorld(WorldDirectory directory, String name){
        if (getWorld(directory, name) != null)
            return false;

        return FileUtil.delete(new File(directory.getDirectory(), name));
    }
}
