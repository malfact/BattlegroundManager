package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.api.doodad.DoodadGroup;

public class DoodadGroupInstance extends DoodadBaseInstance {

    protected TeamColor teamColor;

    public DoodadGroupInstance(BattlegroundInstance battlegroundInstance, DoodadGroup doodad) {
        super(battlegroundInstance, doodad);

        this.teamColor = doodad.getTeamColor();
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(TeamColor teamColor){
        this.teamColor = teamColor != null ? teamColor : TeamColor.DEFAULT;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void tick() {

    }
}
