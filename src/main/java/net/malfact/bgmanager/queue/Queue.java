package net.malfact.bgmanager.queue;

import net.malfact.bgmanager.api.battleground.InstanceManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.response.ResponseListener;
import net.malfact.bgmanager.response.ResponseManager;
import net.malfact.bgmanager.util.Util;
import net.malfact.bgmanager.api.battleground.BattlegroundManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.UUID;

public class Queue extends BukkitRunnable implements ResponseListener {

    public final String id;
    public final Battleground battleground;

    public Queue(String id){
        this.id = id;
        this.battleground = BattlegroundManager.getBattleground(id);
    }

    private final LinkedHashSet<UUID> members = new LinkedHashSet<>();
    private final LinkedHashMap<UUID, String> notified = new LinkedHashMap<>();

    protected void addPlayer(UUID uuid){
        members.add(uuid);
    }

    protected void removePlayer(UUID uuid){
        members.remove(uuid);
        notified.remove(uuid);
    }

    public void notifyPlayers(int count, String instanceId){
        Iterator<UUID> iterator = members.iterator();
        int index = 0;
        while (iterator.hasNext() && index <= count){
            index++;

            UUID playerUUID = iterator.next();
            if (!notified.containsKey(playerUUID)) {
                Util.notifyPlayerBGReady(playerUUID, id);
                notified.put(playerUUID, instanceId);
            }
        }
    }

    @Override
    public void receiveResponse(UUID uuid, boolean confirm) {
        // Ignore if player is not in queue
        if (!members.contains(uuid) || !notified.containsKey(uuid))
            return;

        if (confirm){
            BattlegroundInstance instance = InstanceManager.get().getInstance(id, notified.get(uuid));
            if (instance == null){
                System.out.println("WHAT IS GOING ON");
            } else {
                instance.addPlayer(Bukkit.getPlayer(uuid));
            }
        }

        QueueManager.setPlayerQueue(uuid, null);

        members.remove(uuid);
        notified.remove(uuid);
        ResponseManager.get().unregisterResponseListener(uuid);
    }

    @Override
    public void run() {
        if (!battleground.getEnabled() || members.size() - notified.size() <= 0)
            return;

        BattlegroundInstance[] instances = InstanceManager.get().getInstances(id);
        for (BattlegroundInstance instance : instances){
            if (members.size() - notified.size() <= 0)
                break;

            if (instance.getStatus().allowEntry && instance.getPlayerCount() < instance.getTeamSize()*2){
                    notifyPlayers(instance.getTeamSize()*2 - instance.getPlayerCount(), instance.getInstanceId());
            }
        }

        int available = members.size() - notified.size();
        if (available > 0 && available >= battleground.getTeamSize()*2) {
            BattlegroundInstance instance = battleground.createInstance();
            if (InstanceManager.get().registerInstance(battleground.getId(), instance))
                notifyPlayers(battleground.getTeamSize(), instance.getInstanceId());
        }
    }
}