package net.malfact.bgmanager.api.event;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLeaveInstanceEvent extends PlayerEvent {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final BattlegroundInstance instance;

    public PlayerLeaveInstanceEvent(Player player, BattlegroundInstance instance) {
        super(player);
        this.instance = instance;
    }

    public BattlegroundInstance getInstance() {
        return instance;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
