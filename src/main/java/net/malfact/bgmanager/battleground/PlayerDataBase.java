package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.api.battleground.PlayerData;
import net.malfact.bgmanager.api.battleground.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerDataBase implements PlayerData {

    private final UUID uuid;
    private final TeamColor team;
    private TeamColor flagColor;
    private String flagId;
    private Location deathLocation;
    private boolean dead = false;

    public PlayerDataBase(Player player, TeamColor team) {
        this.uuid = player.getUniqueId();
        this.team = team;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public TeamColor getTeam() {
        return team;
    }

    @Override
    public void setFlag(TeamColor color, String id) {
        this.flagColor = color;
        this.flagId = id;
    }

    @Override
    public TeamColor getFlagColor() {
        return flagColor;
    }

    @Override
    public String getFlagId() {
        return flagId;
    }

    @Override
    public boolean hasFlag() {
        return flagColor != null;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public Location getDeathLocation() {
        return deathLocation.clone();
    }

    @Override
    public void setDeathLocation(Location location) {
        this.deathLocation = location != null ? location.clone() : null;
    }
}
