package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.ApiGetter;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.api.doodad.DoodadOwnable;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class DoodadOwnableBase extends DoodadBase implements DoodadOwnable {

    protected String ownerId = "";
    protected TeamColor teamColor = TeamColor.DEFAULT;

    public DoodadOwnableBase(String id, Battleground battleground) {
        super(id, battleground);
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

    @ApiGetter("teamColor")
    @Override
    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    @Override
    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor != null ? teamColor : TeamColor.DEFAULT;
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("teamColor", teamColor.toString());
        tag.putString("ownerId", ownerId);
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {

        setOwnerId(tag.getString("ownerId"));
        try {
            setTeamColor(TeamColor.valueOf(tag.getString("teamColor")));
        } catch (IllegalArgumentException ignored) {}

        return super.readNBT(tag);
    }

    @EditCommand(cmd ="setTeamColor")
    public void setTeamColor(Player player, String[] args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Team Color is set to " + teamColor.chatColor + teamColor.toString());

            StringBuilder values = new StringBuilder();
            for (TeamColor teamColor : TeamColor.values())
                values.append(teamColor.toString()).append("; ");

            player.sendMessage("Acceptable Values: " + ChatColor.AQUA + values.toString());

            return;
        }

        try {
            TeamColor color = TeamColor.valueOf(args[0]);
            player.sendMessage("<" + id + "> Team Color set to " + color.chatColor + color.toString());
            setTeamColor(color);
        } catch (IllegalArgumentException ignored){
            player.sendMessage(ChatColor.RED + "Invalid Team Color: " + args[0]);
        }
    }
}
