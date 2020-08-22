package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadType;
import net.querz.nbt.tag.CompoundTag;

public abstract class DoodadBase implements Doodad {

    protected final String id;
    protected final Battleground battleground;

    protected boolean debug = false;

    public DoodadBase(String id, Battleground battleground){
        this.id = id;
        this.battleground = battleground;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Battleground getBattleground() {
        return battleground;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (!debug)
            destroy();
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("doodad_type", DoodadType.getByClass(this.getClass()).getName());
        tag.putString("doodad_id", id);

        return tag;
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        return tag;
    }
}
