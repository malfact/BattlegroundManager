package net.malfact.bgmanager.api.battleground;

import net.malfact.bgmanager.battleground.TeamColor;
import org.bukkit.Location;

public interface Team {

    /**
     * Gets the team color for this team
     *
     * @return the team's color
     */
    TeamColor getColor();

    /**
     * Gets the default spawn location for this team
     *
     * @return the location for team spawn
     */
    Location getSpawnLocation();

    /**
     * Sets the default spawn location for this team
     *
     * @param location Location to set
     */
    void setSpawnLocation(Location location);

    /**
     * Create an instance of this team
     *
     * @return Instance of this team
     */
    TeamInstance createInstance(BattlegroundInstance battlegroundInstance);
}
