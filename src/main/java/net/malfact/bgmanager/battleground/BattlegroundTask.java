package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.InstanceManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import net.malfact.bgmanager.queue.Queue;
import net.malfact.bgmanager.queue.QueueManager;
import org.bukkit.scheduler.BukkitRunnable;

public class BattlegroundTask extends BukkitRunnable {

    public final Battleground battleground;
    public final Queue queue;

    public BattlegroundTask(Battleground battleground){
        this.battleground = battleground;
        this.queue = QueueManager.get().getQueue(battleground.getId());
    }

    @Override
    public void run() {
        if (battleground.getDebug())
            battleground.debugTick();

        BattlegroundInstance[] instances = InstanceManager.get().getInstances(battleground.getId());
        for (BattlegroundInstance instance : instances){
            if (instance.getStatus() == BattlegroundStatus.FINISHED){
                InstanceManager.get().deleteInstance(battleground.getId(), instance.getInstanceId());
            } else {
                instance.tick();
            }
        }
    }
}
