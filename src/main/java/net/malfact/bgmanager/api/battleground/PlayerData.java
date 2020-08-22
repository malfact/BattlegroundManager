package net.malfact.bgmanager.api.battleground;

import net.malfact.bgmanager.battleground.TeamColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerData {

    Player getPlayer();

    UUID getUUID();

    TeamColor getTeam();

    void setFlag(TeamColor color, String id);

    TeamColor getFlagColor();

    String getFlagId();

    boolean hasFlag();

    boolean isDead();

    void setDead(boolean dead);

    Location getDeathLocation();

    void setDeathLocation(Location location);
}
