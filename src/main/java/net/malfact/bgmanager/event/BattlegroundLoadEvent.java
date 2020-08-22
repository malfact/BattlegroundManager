package net.malfact.bgmanager.event;

import net.malfact.bgmanager.api.battleground.Battleground;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BattlegroundLoadEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Battleground battleground;

    public BattlegroundLoadEvent(Battleground battleground){
        this.battleground = battleground;
    }

    public Battleground getBattleground() {
        return battleground;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
