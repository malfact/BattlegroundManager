package net.malfact.bgmanager.api.battleground;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface BattlegroundInstance{

    /**
     * Get the battleground Id for this battleground instance
     *
     * @return Id of instance
     */
    String getBattlegroundId();

    /**
     * Get the instance Id for this battleground instance
     *
     * @return Id of instance
     */
    String getInstanceId();

    /**
     * Gets the world that this instance is in
     *
     * @return World the instance is in
     */
    World getWorld();

    /**
     * Get team of color
     *
     * @param teamColor Color of team
     * @return Team of color
     */
    TeamInstance getTeam(TeamColor teamColor);

    /**
     * Add a player to this battleground instance
     *
     * @param player Player to add
     */
    void addPlayer(Player player);

    /**
     * Remove a player from this battleground instance
     *
     * @param player Player to remove
     */
    void removePlayer(Player player);

    /**
     * Gets if player is currently in this battleground instance
     *
     * @param player Player to check
     * @return if a player is in this battleground instance or not
     */
    boolean isPlayerInBattleground(Player player);

    PlayerData getPlayerData(Player player);

    PlayerData[] getPlayerData();

    /**
     * Gets the number of players currently in this battleground instance
     *
     * @return the number of players in this battleground
     */
    int getPlayerCount();

    /**
     * Get spawn location for player in battleground
     *
     * @param player Player to find spawn location for
     * @return Location for player to spawn
     */
    Location getPlayerSpawnLocation(Player player);

    /**
     * Get the team size of this battleground
     *
     * @return Team Size
     */
    int getTeamSize();

    /**
     * Get the current status of this battleground instance
     *
     * @return the status of battleground
     */
    BattlegroundStatus getStatus();

    /**
     * Set the current status of this battleground instance
     *
     * @param status Status to set battleground to
     */
    void setStatus(BattlegroundStatus status);

    /**
     * Broadcast a message to all players in this battleground instance
     *
     * @param msg Message to send
     */
    void broadcast(String msg);

    /**
     * Broadcast a message to all players of a team in this battleground instance
     *
     * @param msg Message to send
     * @param team Team to send to
     */
    void broadcast(String msg, TeamColor team);

    /**
     * Close this battleground instance
     */
    void close();

    /**
     * Core gameloop of battleground instance
     */
    void tick();
}
