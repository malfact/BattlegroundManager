package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.ApiGetter;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.api.doodad.DoodadOwnable;
import net.querz.nbt.tag.CompoundTag;

public abstract class DoodadPhysicalOwnableBase extends DoodadPhysicalBase implements DoodadOwnable {

    protected TeamColor teamColor = TeamColor.DEFAULT;
    protected String ownerId = "";

    public DoodadPhysicalOwnableBase(String id, Battleground battleground) {
        super(id, battleground);
    }

    @ApiGetter("teamColor")
    @Override
    public TeamColor getTeamColor(){
        return this.teamColor;
    }

    @Override
    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor != null ? teamColor : TeamColor.DEFAULT;
    }

    @ApiGetter("ownerId")
    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId != null ? ownerId : "";
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("ownerId", ownerId);
        tag.putString("teamColor", teamColor.toString());
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        setOwnerId(tag.getString("ownerId"));
        try {
            setTeamColor(TeamColor.valueOf(tag.getString("teamColor")));
        } catch (IllegalArgumentException ignored){}

        return super.readNBT(tag);
    }
}
