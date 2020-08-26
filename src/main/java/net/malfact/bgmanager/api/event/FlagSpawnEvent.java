package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import org.bukkit.event.HandlerList;

public class FlagSpawnEvent extends FlagEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final boolean mobile;

    public FlagSpawnEvent(TeamColor flagColor, String flagId, BattlegroundInstance instance, boolean mobile){
        super(flagColor, flagId, instance);
        this.mobile = mobile;
    }

    public boolean isMobile() {
        return mobile;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
