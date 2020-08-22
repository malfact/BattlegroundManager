package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.api.battleground.TeamColor;

public interface DoodadGroup extends DoodadOwnable{

    /**
     * Add a child doodad
     * @param doodad Doodad child
     */
    void addChild(DoodadOwnable doodad);

    /**
     * Remove a child doodad
     * @param doodad Doodad child
     */
    void removeChild(DoodadOwnable doodad);

    /**
     * Get a child doodad object
     * @param doodadId Id of child
     * @return Child doodad, or null if it does not exist
     */
    DoodadOwnable getChild(String doodadId);

    /**
     * Get if a doodad of id is a child
     * @param doodadId Id of child to check
     * @return If Doodad is a child
     */
    boolean isChild(String doodadId);
}
