package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.entity.Player;

public class DoodadFlagCapture extends DoodadRadialField {

    protected TeamColor teamColor = TeamColor.RED;

    public DoodadFlagCapture(String id, Battleground parent) {
        super(id, parent);
        color = teamColor.color;
    }

    public void setTeamColor(TeamColor teamColor){
        this.teamColor = teamColor;
        this.color = teamColor.color;
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadFlagCaptureInstance(battlegroundInstance, this);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("teamColor", teamColor.toString());

        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        setTeamColor(TeamColor.valueOf(tag.getString("teamColor")));

        return super.readNBT(tag);
    }

    @EditCommand(cmd ="setTeamColor")
    public void setTeamColor(Player player, String[] args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Team Color set to " + teamColor.chatColor + teamColor.toString());
            return;
        }

        TeamColor color = TeamColor.valueOf(args[0]);
        player.sendMessage("<" + id + "> Team Color set to " + teamColor.chatColor + teamColor.toString());
        setTeamColor(color);
    }
}
