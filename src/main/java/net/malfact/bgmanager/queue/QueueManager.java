package net.malfact.bgmanager.queue;

import net.malfact.bgmanager.BgManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QueueManager {

    private static final Map<String, Queue> queueRegistry = new HashMap<>();
    private static final Map<UUID, String> playerQueueRegistry = new HashMap<>();

    public static void registerQueue(String id){
        Queue queue = new Queue(id);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BgManager.getInstance(), (Runnable) queue, 0L, 4L);
        queueRegistry.put(id, queue);
    }

    public static void unregisterQueue(String id){
        if (queueRegistry.containsKey(id)) {
            try {
                queueRegistry.get(id).cancel();
            } catch (IllegalStateException ignored) {}
        }

        queueRegistry.remove(id);
    }

    public static Queue getQueue(String id){
        return queueRegistry.get(id);
    }

    public static void setPlayerQueue(UUID uuid, String id){
        // If [Null | ""] remove player from any active queue
        if (id == null || id.equalsIgnoreCase("")) {
            Queue queue = getPlayerQueue(uuid);
            if (queue != null)
                queue.removePlayer(uuid);
            playerQueueRegistry.remove(uuid);
        // If [Else] add player to selected queue
        } else {
            Queue queue = getQueue(id);
            if (queue != null && queue.battleground.getEnabled()) {
                queue.addPlayer(uuid);
                playerQueueRegistry.put(uuid, id);
            }
        }
    }

    public static void setPlayerQueue(Player player, String id){
        setPlayerQueue(player.getUniqueId(), id);
    }

    public static Queue getPlayerQueue(UUID uuid){
        String id = playerQueueRegistry.get(uuid);
        if (id == null)
            return null;

        return queueRegistry.get(id);
    }

    public static Queue getPlayerQueue(Player player){
        return getPlayerQueue(player.getUniqueId());
    }
}
