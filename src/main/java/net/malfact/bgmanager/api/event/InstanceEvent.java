package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import org.bukkit.event.Event;

public abstract class InstanceEvent extends Event {

    private final BattlegroundInstance instance;

    public InstanceEvent(BattlegroundInstance instance){
        this.instance = instance;
    }

    public BattlegroundInstance getInstance() {
        return instance;
    }
}
