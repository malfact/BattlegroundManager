package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import org.bukkit.event.HandlerList;

public class InstanceLoadEvent extends InstanceEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public InstanceLoadEvent(BattlegroundInstance instance) {
        super(instance);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
