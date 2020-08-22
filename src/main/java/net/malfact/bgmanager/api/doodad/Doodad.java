package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.api.NBTContainer;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import org.bukkit.Location;

/**
 * Represents a Doodad in a Battleground
 */
public interface Doodad extends NBTContainer {

    /**
     * Get the Id of this doodad
     *
     * @return Id of doodad
     */
    String getId();

    /**
     * Set the debug value of this dooad
     *
     * @param value
     */
    void setDebug(boolean value);

    /**
     * Create an instance of this doodad
     *
     * @param battlegroundInstance Parent battleground Instance
     * @return Instance of this dooad
     */
    DoodadInstance createInstance(BattlegroundInstance battlegroundInstance);

    /**
     * Destroy this doodad
     */
    void destroy();

    /**
     * Tick loop of this doodad
     */
    void debugTick();
}
