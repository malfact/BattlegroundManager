package net.malfact.bgmanager.queue;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.util.Config;
import org.bukkit.Bukkit;

import java.util.*;

public final class QueueManager {

    private static QueueManager instance = null;

    public static QueueManager get(){
        if (instance == null){
            instance = new QueueManager();
        }

        return instance;
    }

    private final Map<String, Queue> queueRegistry = new HashMap<>();
    private final Map<UUID, String> playerQueueRegistry = new HashMap<>();

    private QueueManager(){}

    public void registerQueue(String id){
        Queue queue = new Queue(id);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BgManager.getInstance(), (Runnable) queue, 0L, 4L);
        queueRegistry.put(id, queue);
    }

    public void unregisterQueue(String id){
        if (queueRegistry.containsKey(id)) {
            try {
                queueRegistry.get(id).cancel();
            } catch (IllegalStateException ignored) {}
        }

        queueRegistry.remove(id);
    }

    public Queue getQueue(String id){
        return queueRegistry.get(id);
    }

    public void setPlayerQueue(UUID uuid, String id){
        // If [Null] remove player from any active queue
        if (id == null) {
            Queue queue = getPlayerQueue(uuid);
            if (queue != null){
                Bukkit.getPlayer(uuid).sendMessage(
                        Config.BG_QUEUE_LEAVE_COLOR + String.format(Config.BG_QUEUE_LEAVE, queue.battleground.getName()));
                queue.removePlayer(uuid);
            }
            playerQueueRegistry.remove(uuid);
        // If [Else] add player to selected queue
        } else {
            Queue queue = getQueue(id);
            if (queue != null && queue.battleground.getEnabled()) {
                Bukkit.getPlayer(uuid).sendMessage(
                        Config.BG_QUEUE_JOIN_COLOR + String.format(Config.BG_QUEUE_JOIN, queue.battleground.getName()));
                queue.addPlayer(uuid);
                playerQueueRegistry.put(uuid, id);
            }
        }
    }

    public Queue getPlayerQueue(UUID uuid){
        String id = playerQueueRegistry.get(uuid);
        if (id == null)
            return null;

        return queueRegistry.get(id);
    }
}
