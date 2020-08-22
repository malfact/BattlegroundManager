package net.malfact.bgmanager;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.battleground.BattlegroundBase;
import net.malfact.bgmanager.battleground.BattlegroundTask;
import net.malfact.bgmanager.queue.QueueManager;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class BattlegroundManager {

    private static BattlegroundManager instance;

    public static BattlegroundManager get(){
        if (instance == null){
            instance = new BattlegroundManager();
        }

        return instance;
    }

    private BattlegroundManager(){}

    private final Map<String, Battleground> battlegroundRegistry = new HashMap<>();
    private final Map<String, BattlegroundTask> battlegroundTaskRegistry = new HashMap<>();

    public boolean RegisterBattleground(BattlegroundBase battleground){
        if (battlegroundRegistry.containsKey(battleground.getId()))
            return false;

        battlegroundRegistry.put(battleground.getId(), battleground);
        QueueManager.get().registerQueue(battleground.getId());

        BattlegroundTask task = new BattlegroundTask(battleground);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BgManager.getInstance(), (Runnable) task, 0L, 1L);
        battlegroundTaskRegistry.put(battleground.getId(), task);

        System.out.println("Registered battleground with id <" + battleground.getId() + ">");

        WorldManager.get().loadWorld(battleground.getId());

        return true;
    }

    public void UnregisterBattleground(String id){
        if (battlegroundRegistry.remove(id) != null){
            QueueManager.get().unregisterQueue(id);
            try {
                battlegroundTaskRegistry.get(id).cancel();
            } catch(IllegalStateException ignored){ }
        }
    }

    public Battleground getBattleground(String id){
        return battlegroundRegistry.get(id);
    }

    public Battleground getBattlegroundByName(String name){
        for (Battleground bg : battlegroundRegistry.values()){
            if (bg.getName().equalsIgnoreCase(name)){
                return bg;
            }
        }

        return null;
    }

    public Battleground getBattleground(String id, String name){
        Battleground bg = getBattlegroundByName(name);
        if (bg == null){
            bg = getBattleground(id);
        }

        return bg;
    }

    public Battleground[] getBattlegrounds(){
        return battlegroundRegistry.values().toArray(new Battleground[0]);
    }

    public void unloadBattlegrounds(){
        //TODO Figure this out
        for (Battleground bg : battlegroundRegistry.values()){
            bg.destroy();
            for (String instanceId : InstanceManager.get().getInstanceIds(bg.getId())){
                InstanceManager.get().getInstance(bg.getId(), instanceId).close();
                InstanceManager.get().deleteInstance(bg.getId(), instanceId);
            }
        }
    }

    public String[] getBattlegroundIds(){
        return battlegroundRegistry.keySet().toArray(new String[0]);
    }

    public void loadBattlegrounds(){
        File folder = new File(BgManager.getInstance().getDataFolder().getAbsoluteFile() + "/battlegrounds");
        folder.mkdirs();
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null){
            return;
        }

        for (File file : listOfFiles){
            try {
                Tag<?> _tag = NBTUtil.read(file).getTag();
                if (_tag instanceof CompoundTag) {
                    CompoundTag tag = (CompoundTag) _tag;
                    BattlegroundBase bg = new BattlegroundBase(tag.getString("id"));
                    bg.readNBT(tag);
                    RegisterBattleground(bg);

                    BgManager.getInstance().getLogger().log(Level.INFO, "Loaded Battleground <" + bg.getId()
                            + "> from " + bg.getId() + ".dat");

                    WorldManager.get().loadWorld(bg.getId());
                }
            } catch (IOException e) {
                BgManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load " + file.getName() + "!");
                e.printStackTrace();
            }
        }
    }

    public void saveBattlegrounds(){
        for (Battleground battleground : getBattlegrounds()){
            saveBattleground(battleground);
        }
    }

    public void saveBattleground(Battleground battleground){
        if (battleground == null)
            return;

        CompoundTag tag = battleground.writeNBT(new CompoundTag());

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

    public void deleteBattleground(String id){
        File file = new File(BgManager.getInstance().getDataFolder().getAbsoluteFile() + "/battlegrounds/" + id
                + ".dat");

        if (file.delete())
            BgManager.getInstance().getLogger().log(Level.INFO, "Deleted Battleground File <" + id + ".dat>!");
        else
            BgManager.getInstance().getLogger().log(Level.WARNING,
                    "Unable to delete Battleground File <" + id + ".dat>!");
    }
}
