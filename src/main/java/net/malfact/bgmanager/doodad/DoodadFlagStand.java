package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DoodadFlagStand extends DoodadPhysicalBase {

    protected DoodadFlagCapture flagCaptureDoodad;
    protected DoodadFlagSpawn flagSpawnDoodad;

    public DoodadFlagStand(String id, Battleground parent) {
        super(id, parent);

        flagCaptureDoodad = new DoodadFlagCapture(id + "_capture", parent);
        flagSpawnDoodad = new DoodadFlagSpawn((id + "_spawn"), parent);
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        flagCaptureDoodad.setLocation(location);
        flagSpawnDoodad.setLocation(location);
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        flagCaptureDoodad.setDebug(debug);
        flagSpawnDoodad.setDebug(debug);
    }

    @Override
    public void destroy() {
        flagCaptureDoodad.destroy();
        flagSpawnDoodad.destroy();
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadFlagStandInstance(battlegroundInstance, this);
    }

    @Override
    public void debugTick() {
        flagSpawnDoodad.debugTick();
        flagCaptureDoodad.debugTick();
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.put("capture", flagCaptureDoodad.writeNBT(new CompoundTag()));
        tag.put("spawn", flagSpawnDoodad.writeNBT(new CompoundTag()));

        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        flagCaptureDoodad.readNBT(tag.getCompoundTag("capture"));
        flagSpawnDoodad.readNBT(tag.getCompoundTag("spawn"));

        return super.readNBT(tag);
    }

    @EditCommand(cmd = "setRadius")
    public void setRadius(Player player, String[] args){
        flagCaptureDoodad.setRadius(player, args);
    }

    @EditCommand(cmd ="setTeamColor")
    public void setTeamColor(Player player, String[] args){
        flagCaptureDoodad.setTeamColor(player, args);
        flagSpawnDoodad.setTeamColor(player, args);
    }

    @EditCommand(cmd ="setRespawnTime")
    public void setRespawnTime(Player player, String[] args){
        flagSpawnDoodad.setRespawnTime(player, args);
    }
}
