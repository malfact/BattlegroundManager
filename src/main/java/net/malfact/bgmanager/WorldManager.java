package net.malfact.bgmanager;

import org.bukkit.*;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class WorldManager {

    private static WorldManager instance;

    private WorldManager(){ }

    public static WorldManager get(){
        if (instance == null)
            instance = new WorldManager();
        return instance;
    }

    public File getBaseDirectory(){
        File folder = new File("Battlegrounds/");
        if (!folder.exists())
            folder.mkdirs();

        return folder;
    }

    public World loadWorld(String name){
        WorldCreator worldCreator = new WorldCreator(getBaseDirectory() + "/" + name);
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
        world.setSpawnLocation(0,64,0);
        return world;
    }

    public void deleteWorldFolder(String name){
        if (Bukkit.getWorld(getBaseDirectory() + "/" + name) != null)
            return;

        deleteDirectory(new File(getBaseDirectory(),name));
    }

    public void copyWorldFolder(String sourceName, String copyName){
        if (Bukkit.getWorld(copyName) != null)
            return;

        BgManager.getInstance().getLogger()
                .log(Level.INFO, "Attempting to copy world <"+sourceName+"> to <"+copyName+">");
        copyDirectory(new File(getBaseDirectory(),sourceName), new File(getBaseDirectory(),copyName));
    }

    public World getWorld(String name){
        return Bukkit.getWorld(getBaseDirectory() + "/" + name);
    }


    private boolean deleteDirectory(File directory){
        File[] contents = directory.listFiles();
        if (contents != null){
            for (File file : contents){
                deleteDirectory(file);
            }
        }
        return directory.delete();
    }

    private void copyDirectory(File source, File target){
        try{
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName())){
                if (source.isDirectory()){
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    String[] files = source.list();
                    for (String file : files){
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyDirectory(srcFile, destFile);
                    }
                } else {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    InputStream in = new FileInputStream(source);
//                    OutputStream out = new FileOutputStream(target);
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = in.read(buffer)) > 0)
//                        out.write(buffer, 0, length);
//                    in.close();
//                    out.close();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
