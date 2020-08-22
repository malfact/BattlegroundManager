package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class DoodadGraveyard extends DoodadRadialField{

    protected TeamColor teamColor = TeamColor.RED;
    protected int respawnTime = 0;

    public DoodadGraveyard(String id, Battleground parent) {
        super(id, parent);
    }

    public void setTeamColor(TeamColor teamColor){
        this.teamColor = teamColor;
        this.color = teamColor.color;
        if (doodadInteractor != null){
            doodadInteractor.setCustomName(teamColor.chatColor + "<GY " + respawnTime + "> " + id);
        }
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
        if (doodadInteractor != null){
            doodadInteractor.setCustomName(teamColor.chatColor + "<GY " + respawnTime + "> " + id);
        }
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadGraveyardInstance(battlegroundInstance, this);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("teamColor", teamColor.toString());
        tag.putInt("respawnTime", respawnTime);
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        setTeamColor(TeamColor.valueOf(tag.getString("teamColor")));
        respawnTime = tag.getInt("respawnTime");
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

    @EditCommand(cmd = "setRespawnTime")
    public void setRespawnTime(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Respawn Time is set to " + ChatColor.AQUA + respawnTime);
            return;
        }

        try {
            int respawnTime = Math.abs(Integer.parseInt(args[0]));
            player.sendMessage("<" + id + "> Respawn Time set to " + ChatColor.AQUA + respawnTime);
            setRespawnTime(respawnTime);
        } catch (NumberFormatException ignored){}
    }

    @Override
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        super.onPlayerInteractAtEntity(event);

        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && event.getPlayer().isSneaking()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Team Color is set to " + teamColor.chatColor + teamColor.toString());
            event.getPlayer().sendMessage(ChatColor.GOLD + "Respawn Time is set to " + ChatColor.AQUA + respawnTime);
        }
    }
}
