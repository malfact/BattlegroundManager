package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import org.bukkit.event.HandlerList;

public class InstanceChangeStatusEvent extends InstanceEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final BattlegroundStatus status;

    public InstanceChangeStatusEvent(BattlegroundInstance instance, BattlegroundStatus status){
        super(instance);
        this.status = status;
    }

    public BattlegroundStatus getStatus() {
        return status;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
