package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.doodad.DoodadPhysical;
import net.malfact.bgmanager.doodad.instance.DoodadBaseInstance;
import org.bukkit.Location;

public abstract class DoodadPhysicalInstance extends DoodadBaseInstance implements DoodadInstance {

    protected final Location location;

    public DoodadPhysicalInstance(BattlegroundInstance instance, DoodadPhysical doodad){
        super(instance, doodad);

        this.location = doodad.getLocation();
        if (location != null)
            this.location.setWorld(instance.getWorld());
    }

    @Override
    public BattlegroundInstance getInstance() {
        return instance;
    }

}
