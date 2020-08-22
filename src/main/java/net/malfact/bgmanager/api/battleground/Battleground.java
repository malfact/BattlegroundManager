package net.malfact.bgmanager.api.battleground;

import net.malfact.bgmanager.api.NBTContainer;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadType;
import org.bukkit.World;

import java.util.Collection;

public interface Battleground extends NBTContainer {

    String getId();

    World getWorld();

    /**
     * Get the name for this battleground
     *
     * @return Name of battleground
     */
    String getName();

    /**
     * Set the name for this battleground
     *
     * @param name Name to change to
     */
    void setName(String name);

    /**
     * Get maximum number of players for this battleground
     *
     * @return Max players
     */
    int getMaxPlayerCount();

    /**
     * Set maximum number of players for this battleground
     *
     * @param maxPlayerCount Amount of players
     */
    void setMaxPlayerCount(int maxPlayerCount);

    /**
     * Get minimum number of players for this battleground
     *
     * @return Min players
     */
    int getMinPlayerCount();

    /**
     * Set minimum number of players for battleground
     *
     * @param minPlayerCount Amount of players
     */
    void setMinPlayerCount(int minPlayerCount);

    /**
     * Gets the length of the battle in seconds
     *
     * @return Length of battle
     */
    int getBattleLength();

    /**
     * Sets the length of the battle in seconds
     *
     * @param battleLength Length of battle
     */
    void setBattleLength(int battleLength);

    /**
     * Gets the win score of battle
     *
     * @return Win score
     */
    int getWinScore();


    /**
     * Sets the win score of battle
     *
     * @param winScore Score to win
     */
    void setWinScore(int winScore);

    /**
     * Set the enabled status of battleground
     *
     * @param enabled Enabled status
     */
    void setEnabled(boolean enabled);

    /**
     * Get the enabled status of battleground
     *
     * @return Enabled status
     */
    boolean getEnabled();

    /**
     * Set the debug status of battleground
     *
     * @param debug Debug stauts
     */
    void setDebug(boolean debug);

    /**
     * Get the debug status of battleground
     *
     * @return Debug status
     */
    boolean getDebug();

    /**
     * Add a doodad to this battleground
     *
     * @param id Id of doodad
     * @param doodad Doodad to add
     * @return The doodad added
     */
    Doodad addDoodad(String id, DoodadType doodad);

    /**
     * Get a doodad of this battleground
     *
     * @param id Id of doodad
     * @return Doodad to get
     */
    Doodad getDoodad(String id);

    /**
     * Remove doodad from battleground
     *
     * @param id Id of doodad
     */
    void removeDoodad(String id);

    Collection<Doodad> getDoodads();

    /**
     * Get a team of color
     *
     * @param team TeamColor of team
     * @return Team of color
     */
    Team getTeam(TeamColor team);

    /**
     * Create an instance of this battleground
     *
     * @return Instance of this battleground
     */
    BattlegroundInstance createInstance();

    void destroy();

    /**
     * Tick loop for editing battleground
     */
    void debugTick();
}
