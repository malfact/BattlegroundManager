package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.doodad.DoodadPhysicalOwnableBase;

public abstract class DoodadPhysicalOwnedInstance extends DoodadPhysicalInstance{

    protected final String ownerId;

    public DoodadPhysicalOwnedInstance(BattlegroundInstance instance, DoodadPhysicalOwnableBase doodad) {
        super(instance, doodad);

        this.ownerId = doodad.getOwnerId();
    }

}
