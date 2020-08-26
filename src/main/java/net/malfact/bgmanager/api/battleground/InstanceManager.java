package net.malfact.bgmanager.api.battleground;

import net.malfact.bgmanager.api.file.FileDirectory;
import net.malfact.bgmanager.api.WorldManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {

    private static InstanceManager instance;

    public static InstanceManager get(){
        if (instance == null)
            instance = new InstanceManager();

        return instance;
    }

    private final Map<String, HashMap<String, BattlegroundInstance>> instanceRegistry = new HashMap<>();

    private InstanceManager(){}

    public boolean registerInstance(String battlegroundId, BattlegroundInstance instance){
        if (instance == null)
            return false;

        if (!instanceRegistry.containsKey(battlegroundId))
            instanceRegistry.put(battlegroundId, new HashMap<>());

        instanceRegistry.get(battlegroundId).put(instance.getInstanceId(), instance);

        return true;
    }

    public void deleteInstance(String battlegroundId, String instanceId){
        if (!instanceRegistry.containsKey(battlegroundId))
            return;

        if (!instanceRegistry.get(battlegroundId).containsKey(instanceId))
            return;

        BattlegroundInstance instance = instanceRegistry.get(battlegroundId).get(instanceId);
        instance.close();

        WorldManager.unloadWorld(FileDirectory.WORLD_INSTANCE, instanceId, false);
        WorldManager.deleteWorld(FileDirectory.WORLD_INSTANCE, instanceId);
    }

    public BattlegroundInstance getInstance(String battlegroundId, String instanceId){
        if (!instanceRegistry.containsKey(battlegroundId))
            return null;

        return instanceRegistry.get(battlegroundId).get(instanceId);
    }

    public BattlegroundInstance[] getInstances(String battlegroundId){
        if (!instanceRegistry.containsKey(battlegroundId))
            return new BattlegroundInstance[0];

        return instanceRegistry.get(battlegroundId).values().toArray(new BattlegroundInstance[0]);
    }

    public String[] getInstanceIds(String battlegroundId){
        if (!instanceRegistry.containsKey(battlegroundId))
            return new String[0];

        BattlegroundInstance[] instances = getInstances(battlegroundId);
        String[] instanceIds = new String[instances.length];

        for (int i = 0; i < instances.length; i++){
            instanceIds[i] = instances[i].getInstanceId();
        }

        return instanceIds;
    }

    public BattlegroundInstance getPlayerInstance(Player player){
        for (HashMap<String, BattlegroundInstance> instanceList : instanceRegistry.values()){
            for (BattlegroundInstance instance : instanceList.values()){
                if (instance.isPlayerInBattleground(player)){
                    return instance;
                }
            }
        }

        return null;
    }
}
