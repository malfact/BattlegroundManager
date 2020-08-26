package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import org.bukkit.event.Event;

public abstract class FlagEvent extends Event {

    protected final TeamColor flagColor;
    protected final String flagId;
    protected final BattlegroundInstance instance;

    public FlagEvent(TeamColor flagColor, String flagId, BattlegroundInstance instance){
        this.flagColor = flagColor;
        this.flagId = flagId;
        this.instance = instance;
    }

    public TeamColor getFlagColor() {
        return flagColor;
    }

    public String getFlagId() {
        return flagId;
    }

    public BattlegroundInstance getInstance() {
        return instance;
    }
}
