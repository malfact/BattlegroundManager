package net.malfact.bgmanager.api;

import org.bukkit.event.player.PlayerEvent;

public interface PlayerEventListener<T extends PlayerEvent> {

    void onPlayerEvent(T event);
}
