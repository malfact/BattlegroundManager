package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.ProgressBar;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.PlayerData;
import net.malfact.bgmanager.battleground.TeamColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class DoodadGraveyardInstance extends DoodadRadialFieldInstance {

    protected final TeamColor teamColor;
    protected final int respawnTime;
    protected int timer;

    protected final ProgressBar timerBar;

    protected DoodadGraveyardInstance(BattlegroundInstance battlegroundInstance, DoodadGraveyard doodad) {
        super(battlegroundInstance, doodad);
        this.teamColor = doodad.teamColor;
        this.respawnTime = doodad.respawnTime;
        this.timerBar = new ProgressBar("Graveyard", BarColor.YELLOW, BarStyle.SOLID, ProgressBar.TitleFlag.TIME);
        this.timerBar.setMaxValue(respawnTime);

        timer = respawnTime*20;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public Location getLocation(){
        return location != null ? location.clone() : null;
    }

    @Override
    public void destroy() {
        super.destroy();
        timerBar.removeAllPlayers();
    }

    @Override
    public void tick() {
        timer--;

        timerBar.setCurrentValue(timer/20);

        for (PlayerData playerData : battlegroundInstance.getPlayerData()){
            if (playerData.isDead()){
                double inc = (Math.PI / (radius * 10));
                for (double i = 0; i < Math.PI * 2; i += inc) {
                    Location l = location.clone();
                    playerData.getPlayer().spawnParticle(
                            Particle.REDSTONE,
                            l.add(radius * Math.sin(i), 0.0, radius * Math.cos(i)),
                            1,
                            new Particle.DustOptions(Color.YELLOW, 0.5f)
                    );
                }

                if (isPlayerInRadius(playerData.getPlayer())){
                    timerBar.addPlayer(playerData.getPlayer());
                } else {
                    timerBar.removePlayer(playerData.getPlayer());
                }
            }
        }

        if (timer <= 0){
            timer = respawnTime*20;

            for (PlayerData playerData : battlegroundInstance.getPlayerData()){
                if (playerData.isDead()
                        && playerData.getTeam() == teamColor
                        && isPlayerInRadius(playerData.getPlayer())) {
                    playerData.setDeathLocation(null);
                    playerData.setDead(false);
                    timerBar.removePlayer(playerData.getPlayer());
                }
            }
        }
    }

    public boolean canPlayerSpawn(Player player){
        PlayerData playerData = battlegroundInstance.getPlayerData(player);

        if (playerData == null)
            return false;

        return playerData.getTeam() == teamColor;
    }
}
