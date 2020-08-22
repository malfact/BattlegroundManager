package net.malfact.bgmanager.api.doodad;

import org.bukkit.Location;

/**
 * Represents a Physical Doodad in a battleground
 */
public interface DoodadPhysical extends Doodad {

    /**
     * Set the location of this doodad
     *
     * @param location Location to set
     */
    void setLocation(Location location);

    /**
     * Get the location of this doodad
     *
     * @return Location of doodad
     */
    Location getLocation();
}
