package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.api.battleground.TeamColor;

public interface DoodadOwnable extends Doodad{

    /**
     * Get the TeamColor of this Doodad
     * @return TeamColor of group
     */
    TeamColor getTeamColor();

    /**
     * Set the TeamColor of this Doodad
     * @param teamColor TeamColor to set
     */
    void setTeamColor(TeamColor teamColor);

    /**
     * Get the OwnerId of this Doodad
     * @return OwnerId of Doodad
     */
    String getOwnerId();

    /**
     * Set the OwnerId of this Doodad
     * @param ownerId OwnerId of Doodad
     */
    void setOwnerId(String ownerId);
}
