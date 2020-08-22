package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.PlayerData;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.doodad.DoodadFlagCapture;
import net.malfact.bgmanager.event.FlagCaptureEvent;
import net.malfact.bgmanager.event.FlagDespawnEvent;
import net.malfact.bgmanager.event.FlagSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DoodadFlagCaptureInstance extends DoodadRadialFieldInstance implements Listener {

    protected final TeamColor teamColor;

    // Used to set active state if paired with a DoodadFlagSpawn
    protected final String flagId;

    public DoodadFlagCaptureInstance(BattlegroundInstance battlegroundInstance, DoodadFlagCapture doodad) {
        super(battlegroundInstance, doodad);
        this.teamColor = doodad.getTeamColor();

        this.flagId = doodad.getFlagId();
        BgManager.registerListener(this);
    }

    public String getFlagId() {
        return flagId;
    }

    @Override
    public void destroy() {
        super.destroy();
        BgManager.unregisterListener(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (active) {
            for (PlayerData playerData : instance.getPlayerData()){
                if (playerData.getPlayer().getLocation().distance(location) <= radius){
                    if (playerData.hasFlag() && playerData.getFlagColor() != teamColor && playerData.getTeam() == teamColor){
                        instance.broadcast(playerData.getTeam().chatColor
                                + playerData.getPlayer().getDisplayName() + " has captured the "
                                + playerData.getFlagColor().toString() +" Team's flag!");
                        instance.getTeam(playerData.getTeam()).addScore(1);

                        Bukkit.getPluginManager().callEvent(
                                new FlagCaptureEvent(playerData.getFlagColor(), playerData.getFlagId(), instance, playerData.getPlayer(), playerData.getTeam())
                        );

                        playerData.setFlag(null, "");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFlagSpawnEvent(FlagSpawnEvent event){
        if (event.getInstance() != this.getInstance()) return;

        if (!event.isMobile() && event.getFlagId().equals(flagId)){
            this.active = true;
        }
    }

    @EventHandler
    public void onFlagDespawnEvent(FlagDespawnEvent event){
        if (event.getInstance() != this.getInstance()) return;

        if (!event.isMobile() && event.getFlagId().equals(flagId)){
            this.active = false;
        }
    }
}
