package net.malfact.bgmanager;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.world.WorldDirectory;
import net.malfact.bgmanager.api.world.WorldManager;
import net.malfact.bgmanager.battleground.BattlegroundBase;
import net.malfact.bgmanager.battleground.BattlegroundTask;
import net.malfact.bgmanager.event.BattlegroundLoadEvent;
import net.malfact.bgmanager.queue.QueueManager;
import net.malfact.bgmanager.util.Config;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class BattlegroundManager {

    private static final Map<String, Battleground> battlegroundRegistry = new HashMap<>();
    private static final Map<String, BattlegroundTask> battlegroundTaskRegistry = new HashMap<>();

    public static boolean registerBattleground(BattlegroundBase battleground){
        if (battlegroundRegistry.containsKey(battleground.getId())) {
            BgManager.getInstance().getLogger().log(Level.WARNING, "Unable to register Battleground with id <"
                    + battleground.getId() + "> Battleground with this id is already registered!");
            return false;
        }

        battlegroundRegistry.put(battleground.getId(), battleground);
        QueueManager.get().registerQueue(battleground.getId());

        BattlegroundTask task = new BattlegroundTask(battleground);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BgManager.getInstance(), (Runnable) task, 0L, 1L);
        battlegroundTaskRegistry.put(battleground.getId(), task);

        BgManager.getInstance().getLogger().log(Level.INFO, "Registered battleground with id <"
                + battleground.getId() + ">");

        return true;
    }

    public static void unregisterBattleground(String id){
        if (battlegroundRegistry.remove(id) != null){
            QueueManager.get().unregisterQueue(id);
            try {
                battlegroundTaskRegistry.get(id).cancel();
            } catch(IllegalStateException ignored){ }

            BgManager.getInstance().getLogger().log(Level.INFO, "Unregistered battleground with id <" + id + ">");
        }
    }

    public static Battleground getBattleground(String id){
        return battlegroundRegistry.get(id);
    }

    public static Battleground getBattlegroundByName(String name){
        for (Battleground bg : battlegroundRegistry.values()){
            if (bg.getName().equalsIgnoreCase(name)){
                return bg;
            }
        }

        return null;
    }

    @Deprecated
    public static Battleground getBattleground(String id, String name){
        Battleground bg = getBattlegroundByName(name);
        if (bg == null){
            bg = getBattleground(id);
        }

        return bg;
    }

    public static Battleground[] getBattlegrounds(){
        return battlegroundRegistry.values().toArray(new Battleground[0]);
    }

    public static void unloadBattlegrounds(){
        //TODO Figure this out
        for (Battleground bg : battlegroundRegistry.values()){
            bg.destroy();
            for (String instanceId : InstanceManager.get().getInstanceIds(bg.getId())){
                InstanceManager.get().getInstance(bg.getId(), instanceId).close();
                InstanceManager.get().deleteInstance(bg.getId(), instanceId);
            }
        }
    }

    public static String[] getBattlegroundIds(){
        return battlegroundRegistry.keySet().toArray(new String[0]);
    }

    public static void loadBattlegrounds(){
        File folder = new File(BgManager.getInstance().getDataFolder().getAbsoluteFile() + "/battlegrounds");
        folder.mkdirs();
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null){
            return;
        }

        for (File file : listOfFiles){
            try {
                NamedTag namedTag = NBTUtil.read(file);
                if (namedTag == null) {
                    BgManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load " + file.getName() + "! No tag found!");
                    continue;
                }
                Tag<?> _tag = namedTag.getTag();

                if (!(_tag instanceof CompoundTag)){
                    BgManager.getInstance().getLogger().log(Level.SEVERE, "Unable to load " + file.getName() + "! Tag is not of type (CompoundTag)!");
                    continue;
                }

                CompoundTag tag = (CompoundTag) _tag;

                /*if (!tag.containsKey("_version") || tag.getString("_version").equalsIgnoreCase(Config.BG_SAVE_VERSION)) {
                    BgManager.getInstance().getLogger().log(Level.SEVERE, "Unable to load " + file.getName() + "! Invalid Save Version!");
                    BgManager.getInstance().getLogger().log(Level.SEVERE, tag.getString("_version") + "!=" + Config.BG_SAVE_VERSION);
                    continue;
                }

                if (!tag.containsKey("_type") || tag.getString("_type").equalsIgnoreCase("battleground")) {
                    BgManager.getInstance().getLogger().log(Level.SEVERE, "Unable to load " + file.getName() + "! Not a battleground save file!");
                    continue;
                }*/

                BattlegroundBase bg = new BattlegroundBase(tag.getString("id"));
                bg.readNBT(tag);
                registerBattleground(bg);

                BgManager.getInstance().getLogger().log(Level.INFO, "Loaded Battleground <" + bg.getId()
                        + "> from " + bg.getId() + ".dat");

                WorldManager.loadWorld(WorldDirectory.SAVE, bg.getId());

                Bukkit.getPluginManager().callEvent(new BattlegroundLoadEvent(bg));
            } catch (IOException e) {
                BgManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load " + file.getName() + "! File must be corrupted!");
            }
        }
    }

    public static void saveBattlegrounds(){
        for (Battleground battleground : getBattlegrounds()){
            saveBattleground(battleground);
        }
    }

    public static void saveBattleground(Battleground battleground){
        if (battleground == null)
            return;

        CompoundTag tag = battleground.writeNBT(new CompoundTag());
        tag.putString("_version", Config.BG_SAVE_VERSION);
        tag.putString("_type", "battleground");

        try {
            File folder = new File(BgManager.getInstance().getDataFolder().getAbsoluteFile()
                    + "/battlegrounds");
            folder.mkdirs();
            NBTUtil.write(tag,  folder + "/" + battleground.getId() + ".dat");

            BgManager.getInstance().getLogger().log(Level.INFO, "Saved Battleground <" + battleground.getId()
                    + "> to "
                    + battleground.getId() + ".dat!");
        } catch (IOException e) {
            BgManager.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
        }
    }

    public static void deleteBattleground(String id){
        File file = new File(BgManager.getInstance().getDataFolder().getAbsoluteFile() + "/battlegrounds/" + id
                + ".dat");

        if (file.delete())
            BgManager.getInstance().getLogger().log(Level.INFO, "Deleted Battleground File <" + id + ".dat>!");
        else
            BgManager.getInstance().getLogger().log(Level.WARNING,
                    "Unable to delete Battleground File <" + id + ".dat>!");
    }
}
