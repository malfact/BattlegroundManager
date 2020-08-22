package net.malfact.bgmanager.api.battleground;

import net.malfact.bgmanager.battleground.TeamColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface TeamInstance{

    /**
     * Get the color of this team
     *
     * @return Color of team
     */
    TeamColor getColor();

    /**
     * Gets the current score for this team
     *
     * @return Score for team
     */
    int getScore();

    /**
     * Sets the current score for this team
     *
     * @param score Score to set
     */
    void setScore(int score);

    /**
     * Add amount to the current score for this team
     *
     * @param amount Amount to add to score
     */
    void addScore(int amount);

    /**
     * Add a player to the team
     *
     * @param player Player to add to team
     */
    void addPlayer(Player player);

    /**
     * Remove a player from the team
     *
     * @param player Player to remove from team
     */
    void removePlayer(Player player);

    /**
     * Get if player is a member of team
     *
     * @param player Player to check
     */
    boolean hasPlayer(Player player);

    /**
     * Get the amount of players on this team
     *
     * @return Number of players
     */
    int getSize();

    /**
     * Get the spawn location for this team
     *
     * @return Location of spawn
     */
    Location getSpawnLocation();
}
