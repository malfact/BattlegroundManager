package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadInstance;

public abstract class DoodadBaseInstance implements DoodadInstance {

    protected final BattlegroundInstance instance;
    protected final String id;

    public DoodadBaseInstance(BattlegroundInstance instance, Doodad doodad){
        this.instance = instance;
        this.id = doodad.getId();
    }

    @Override
    public BattlegroundInstance getInstance() {
        return instance;
    }
}
