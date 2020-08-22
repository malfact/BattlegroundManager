package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.doodad.DoodadPhysical;
import org.bukkit.Location;

public abstract class DoodadPhysicalBaseInstance implements DoodadInstance {

    protected final BattlegroundInstance battlegroundInstance;
    protected final String id;
    protected final Location location;

    protected DoodadPhysicalBaseInstance(BattlegroundInstance battlegroundInstance, DoodadPhysical doodad){

        this.battlegroundInstance = battlegroundInstance;
        this.id = doodad.getId();
        this.location = doodad.getLocation();
        if (location != null)
            this.location.setWorld(battlegroundInstance.getWorld());
    }

    @Override
    public void destroy(){}

    @Override
    public void tick(){}

    @Override
    public BattlegroundInstance getBattlegroundInstance() {
        return battlegroundInstance;
    }

}
