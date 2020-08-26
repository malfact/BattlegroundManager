package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.ApiGetter;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.doodad.instance.DoodadFlagCaptureInstance;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DoodadFlagCapture extends DoodadRadialFieldBase {

    protected String flagId = "";

    public DoodadFlagCapture(String id, Battleground parent) {
        super(id, parent);
    }

    public void setFlagId(String flagId) {
        this.flagId = flagId != null ? flagId : "";
    }

    @ApiGetter("flagId")
    public String getFlagId() {
        return flagId;
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadFlagCaptureInstance(battlegroundInstance, this);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("flagId", flagId);
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        setFlagId(tag.getString("flagId"));
        return super.readNBT(tag);
    }

    @EditCommand(cmd ="setFlagId")
    public void setFlagId(Player player, String[] args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Flag Id is set to " + ChatColor.AQUA + "\"" + flagId + "\"");
            return;
        } else if (args.length > 1){
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return;
        }

        args[0] = args[0].replaceAll("\"", "");

        player.sendMessage("<" + id + "> Flag Id set to " + ChatColor.AQUA + "\"" + args[0] + "\"");
        setFlagId(args[0]);
    }
}
