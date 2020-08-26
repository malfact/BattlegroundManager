package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FlagPickupEvent extends FlagEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    protected final Player player;
    protected final TeamColor playerTeamColor;

    public FlagPickupEvent(TeamColor flagColor, String flagId, BattlegroundInstance instance, Player player, TeamColor playerTeamColor){
        super(flagColor, flagId, instance);
        this.player = player;
        this.playerTeamColor = playerTeamColor;
    }

    public Player getPlayer() {
        return player;
    }

    public TeamColor getPlayerTeamColor() {
        return playerTeamColor;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
