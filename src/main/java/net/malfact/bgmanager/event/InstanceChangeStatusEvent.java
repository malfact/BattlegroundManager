package net.malfact.bgmanager.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InstanceChangeStatusEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final BattlegroundInstance battleground;
    private final BattlegroundStatus status;

    public InstanceChangeStatusEvent(BattlegroundInstance battleground, BattlegroundStatus status){
        this.battleground = battleground;
        this.status = status;
    }

    public BattlegroundInstance getBattleground() {
        return battleground;
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
