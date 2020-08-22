package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.Team;
import net.malfact.bgmanager.api.battleground.TeamInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamBaseInstance implements TeamInstance {

    protected  final BattlegroundInstance battlegroundInstance;

    protected final TeamColor color;
    protected final Location spawnLocation;
    protected final Set<UUID> players = new HashSet<>();

    protected int score = 0;

    public TeamBaseInstance(Team baseTeam, BattlegroundInstance battlegroundInstance) {
        this.color = baseTeam.getColor();
        this.spawnLocation = baseTeam.getSpawnLocation();

        this.battlegroundInstance = battlegroundInstance;
    }

    @Override
    public TeamColor getColor() {
        return this.color;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void addScore(int amount) {
        this.score += amount;
    }

    @Override
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    @Override
    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    @Override
    public int getSize() {
        return players.size();
    }

    @Override
    public Location getSpawnLocation() {
        Location l = spawnLocation.clone();
        l.setWorld(battlegroundInstance.getWorld());
        return l;
    }
}
