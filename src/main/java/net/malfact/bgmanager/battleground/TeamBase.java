package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.Team;
import net.malfact.bgmanager.api.battleground.TeamInstance;
import org.bukkit.Location;

public class TeamBase implements Team {

    protected final TeamColor color;
    protected Location spawnLocation;

    public TeamBase(TeamColor color){
        this.color = color;
    }

    @Override
    public TeamColor getColor() {
        return color;
    }

    @Override
    public Location getSpawnLocation() {
        if (spawnLocation != null)
            return spawnLocation.clone();
        else
            return null;
    }

    @Override
    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }

    @Override
    public TeamInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new TeamBaseInstance(this, battlegroundInstance);
    }
}
