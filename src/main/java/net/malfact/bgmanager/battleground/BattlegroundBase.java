package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.WorldManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.Team;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadType;
import net.malfact.bgmanager.util.Config;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class BattlegroundBase implements Battleground {

    protected final String id;
    protected String name = "PlaceHolder";
    protected int minPlayers = 2;
    protected int maxPlayers = 12;
    protected int battleLength = 1500; //1500 seconds = 25 minutes
    protected int winScore = 3;

    protected final Map<String, Doodad> doodads = new HashMap<>();
    protected final Map<TeamColor, Team> teams = new HashMap<>();

    protected boolean enabled = false;

    protected boolean debug = false;

    public BattlegroundBase(String id){
        this.id = id;
        teams.put(TeamColor.RED, new TeamBase(TeamColor.RED));
        teams.put(TeamColor.BLUE, new TeamBase(TeamColor.BLUE));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public World getWorld() {
        return WorldManager.get().getWorld(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayers;
    }

    @Override
    public int getMinPlayerCount() {
        return minPlayers;
    }

    @Override
    public void setMaxPlayerCount(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void setMinPlayerCount(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    @Override
    public int getBattleLength() {
        return battleLength;
    }

    @Override
    public void setBattleLength(int battleLength) {
        this.battleLength = battleLength;
    }

    @Override
    public int getWinScore() {
        return winScore;
    }

    @Override
    public void setWinScore(int winScore) {
        this.winScore = winScore;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled){
            for (Team team : teams.values()){
                if (team.getSpawnLocation() == null)
                    return;
            }
        }
        this.enabled = enabled;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        doodads.values().forEach(doodad -> doodad.setDebug(debug));

        if(!debug)
            this.getWorld().save();
    }

    @Override
    public boolean getDebug() {
        return debug;
    }

    @Override
    public Doodad addDoodad(String id, DoodadType doodadType) {
        if (id == null || doodadType == null)
            return null;
        if (doodads.containsKey(id))
            return null;

        try {
            Constructor<?> constructor = doodadType.getDoodadClass().getConstructor(String.class, Battleground.class);
            Doodad doodad = (Doodad) constructor.newInstance(id, this);
            doodads.put(id, doodad);

            return doodad;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            BgManager.getInstance().getLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    @Override
    public Doodad getDoodad(String id) {
        return doodads.get(id);
    }

    @Override
    public Collection<Doodad> getDoodads() {
        return doodads.values();
    }

    @Override
    public void removeDoodad(String id) {
        if (!doodads.containsKey(id))
            return;

        Doodad doodad = doodads.get(id);
        doodads.remove(id);
        doodad.destroy();
    }

    @Override
    public Team getTeam(TeamColor team) {
        return teams.get(team);
    }

    @Override
    public BattlegroundInstance createInstance() {
        return new BattlegroundBaseInstance(this);
    }

    @Override
    public void destroy() {
        doodads.values().forEach(Doodad::destroy);
        this.getWorld().save();
    }

    @Override
    public void debugTick() {
        doodads.values().forEach(Doodad::debugTick);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag){
        tag.putString("id", id);
        tag.putString("name", name);
        tag.putBoolean("enabled", enabled);
        tag.putInt("maxPlayers", maxPlayers);
        tag.putInt("minPlayers", minPlayers);
        tag.putInt("battleLength", battleLength);
        tag.putInt("winScore", winScore);

        CompoundTag teamsTag = new CompoundTag();

        for (Team team : teams.values()) {
            CompoundTag teamTag = new CompoundTag();
            if (team.getSpawnLocation() != null) {
                CompoundTag locationTag = new CompoundTag();

                locationTag.putString("world", WorldManager.get().getBaseDirectory() + id);
                locationTag.putDouble("x", team.getSpawnLocation().getX());
                locationTag.putDouble("y", team.getSpawnLocation().getY());
                locationTag.putDouble("z", team.getSpawnLocation().getZ());
                locationTag.putFloat("yaw", team.getSpawnLocation().getYaw());
                locationTag.putFloat("pitch", team.getSpawnLocation().getPitch());

                teamTag.put("location", locationTag);
            }

            teamsTag.put(team.getColor().toString(), teamTag);
        }

        tag.put("teams", teamsTag);

        ListTag<CompoundTag> doodadsList = new ListTag<>(CompoundTag.class);
        if (doodads.size() > 0){
            doodads.values().forEach(doodad -> doodadsList.add(doodad.writeNBT(new CompoundTag())));
        }
        tag.put("doodads", doodadsList);

        return tag;
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag){
        name = tag.getString("name");
        maxPlayers = tag.getInt("maxPlayers");
        minPlayers = tag.getInt("minPlayers");
        enabled = tag.getBoolean("enabled");
        battleLength = tag.getInt("battleLength");
        winScore = tag.getInt("winScore");

        CompoundTag teamsTag = tag.getCompoundTag("teams");

        if (teamsTag != null){
            teamsTag.keySet().forEach(k -> {
                CompoundTag teamTag = teamsTag.getCompoundTag(k);
                Team team = teams.get(TeamColor.valueOf(k));

                CompoundTag locationTag = teamTag.getCompoundTag("location");
                if (locationTag != null) {
                    team.setSpawnLocation(
                            new Location(
                                    Bukkit.getWorld(locationTag.getString("world")),
                                    locationTag.getDouble("x"),
                                    locationTag.getDouble("y"),
                                    locationTag.getDouble("z"),
                                    locationTag.getFloat("yaw"),
                                    locationTag.getFloat("pitch")
                            )
                    );
                }
            });
        }

        ListTag<CompoundTag> doodadsList = tag.getListTag("doodads").asCompoundTagList();

        for (CompoundTag doodadTag : doodadsList){
            String id = doodadTag.getString("doodad_id");
            Doodad doodad = addDoodad(id, DoodadType.getByName(doodadTag.getString("doodad_type")));
            if (doodad != null)
                doodad.readNBT(doodadTag);
            else
                BgManager.getInstance().getLogger().log(Level.WARNING, "Error loading Doodad " + id + "! Skipping!");
        }

        return tag;
    }

    @EditCommand(cmd = "setname")
    public void setName(Player player, String[] args){
        if (args.length == 0)
            return;
        StringBuilder newName = new StringBuilder(args[0]);

        for (int i = 1; i < args.length; i++){
            newName.append(" ").append(args[i]);
        }

        String newNameOut = newName.toString();
        player.sendMessage(
                String.format(Config.BG_CMD_SETNAME, id, newNameOut, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT)
        );

        setName(newNameOut);
    }

    @EditCommand(cmd = "toggledebug")
    public void toggleDebug(Player player, String[] args){
        player.sendMessage(String.format("%4$s<%1$s>%3$s edit mode is now %5$s[%2$s]",
                id, !debug ? "Enabled" : "Disabled", Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT,
                !debug ? ChatColor.GREEN : ChatColor.RED)
        );
        setDebug(!debug);
    }

    @EditCommand(cmd = "setenabled")
    public void setEnabled(Player player, String[] args){
        if (args.length == 0)
            return;

        if (!args[0].equalsIgnoreCase("true") && !args[0].equalsIgnoreCase("false"))
            return;

        boolean enabled = Boolean.parseBoolean(args[0]);

        if (enabled){
            boolean canEnable = true;

            for (Team team : teams.values()){
                if (team.getSpawnLocation() == null){
                    player.sendMessage(ChatColor.RED +  " > " +team.getColor().toString()
                            + " Team must have a spawn location!");
                    canEnable = false;
                }
            }

            if (!canEnable){
                player.sendMessage(ChatColor.RED + "These errors must be fixed before enabling!");
                return;
            }
        }

        player.sendMessage(
            String.format(Config.BG_CMD_SETENABLED, id, enabled ? "Enabled" : "Disabled", Config.COMMAND_COLOR,
                Config.COMMAND_HIGHLIGHT, enabled ? ChatColor.GREEN : ChatColor.RED)
        );

        setEnabled(enabled);
    }

    @EditCommand(cmd = "setmaxplayercount")
    public void setMaxPlayerCount(Player player, String[] args){
        if (args.length == 0)
            return;

        try {
            int value = Integer.parseInt(args[0]);
            player.sendMessage(
                String.format(Config.BG_CMD_SETMAXPLAYERS, id, value, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT)
            );
            setMaxPlayerCount(value);
        } catch (NumberFormatException e){
            player.sendMessage(ChatColor.RED + "Value must be an integer!");
        }
    }

    @EditCommand(cmd = "setminplayercount")
    public void setMinPlayerCount(Player player, String[] args){
        if (args.length == 0)
            return;

        try {
            int value = Integer.parseInt(args[0]);
            player.sendMessage(
                    String.format(Config.BG_CMD_SETMINPLAYERS, id, value, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT)
            );
            setMinPlayerCount(value);
        } catch (NumberFormatException e){
            player.sendMessage(ChatColor.RED + "Value must be an integer!");
        }
    }

    @EditCommand(cmd = "setbattlelength")
    public void setBattleLength(Player player, String[] args){
        if (args.length == 0)
            return;

        try {
            int value = Math.abs(Integer.parseInt(args[0]));
            player.sendMessage(
                    String.format("%3$sBattleground %4$s<%1$s>%3$s Battle Length set to %4$s<%2$s>"
                            , id, value, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT)
            );
            setBattleLength(value);
        } catch (NumberFormatException e){
            player.sendMessage(ChatColor.RED + "Value must be an integer!");
        }
    }

    @EditCommand(cmd = "setwinscore")
    public void setWinScore(Player player, String[] args){
        if (args.length == 0)
            return;

        try {
            int value = Math.abs(Integer.parseInt(args[0]));
            player.sendMessage(
                    String.format("%3$sBattleground %4$s<%1$s>%3$s Win Score set to %4$s<%2$s>"
                            , id, value, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT)
            );
            setWinScore(value);
        } catch (NumberFormatException e){
            player.sendMessage(ChatColor.RED + "Value must be an integer!");
        }
    }

    @EditCommand(cmd = "getvalues")
    public void getValues(Player player, String[] args){
        player.sendMessage(ChatColor.GOLD+"<Id> = <" + id + ">");
        player.sendMessage(ChatColor.GOLD+"<Enabled> = <" + enabled + ">");
        player.sendMessage(ChatColor.GOLD+"<Name> = <" + name + ">");
        player.sendMessage(ChatColor.GOLD+"<MinPlayers> = <" + minPlayers + ">");
        player.sendMessage(ChatColor.GOLD+"<MaxPlayers> = <" + maxPlayers + ">");
        player.sendMessage(ChatColor.GOLD+"<BattleLength> = <" + battleLength + ">");
        player.sendMessage(ChatColor.GOLD+"<WinScore> = <" + winScore + ">");
    }
}
