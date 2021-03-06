package net.malfact.bgmanager.battleground;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.ProgressBar;
import net.malfact.bgmanager.api.WorldManager;
import net.malfact.bgmanager.api.battleground.*;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.event.InstanceChangeStatusEvent;
import net.malfact.bgmanager.api.event.InstanceLoadEvent;
import net.malfact.bgmanager.api.event.PlayerJoinInstanceEvent;
import net.malfact.bgmanager.api.event.PlayerLeaveInstanceEvent;
import net.malfact.bgmanager.api.file.FileDirectory;
import net.malfact.bgmanager.doodad.instance.DoodadGraveyardInstance;
import net.malfact.bgmanager.util.Config;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class BattlegroundBaseInstance implements BattlegroundInstance, Listener {

    // Variables inherited from Battleground
    protected final String battlegroundId;
    protected final String name;
    protected final int teamSize;
    protected final int battleLength;
    protected final int winScore;

    // Data Storage
    protected final Map<String, DoodadInstance> doodads = new HashMap<>();
    protected final Map<UUID, PlayerData> playerData = new HashMap<>();
    protected final HashMap<TeamColor, TeamInstance> teams = new HashMap<>();

    // Local Variables
    protected final String instanceId;
    protected final World world;
    protected BattlegroundStatus status = BattlegroundStatus.INIT;
    protected final Scoreboard scoreboard;
    protected ProgressBar timerBar;

    private int timer = 0;
    private int notEnoughPlayersTimer = 0;

    protected BattlegroundBaseInstance(BattlegroundBase baseBattleground){

        // Assign variables inherited from Battleground;
        this.battlegroundId = baseBattleground.id;
        this.name = baseBattleground.name;
        this.teamSize = baseBattleground.teamSize;
        this.battleLength = baseBattleground.battleLength;
        this.winScore = baseBattleground.winScore;

        // Assign InstanceId
        this.instanceId = UUID.randomUUID().toString();

        // Load World
        baseBattleground.setDebug(false);
        world = WorldManager.copyWorld(FileDirectory.WORLD_SAVE, battlegroundId, FileDirectory.WORLD_INSTANCE, instanceId);
        world.setAutoSave(false);

        // Instantiate teams
        for(Team team : baseBattleground.teams.values()){
            teams.put(team.getColor(), team.createInstance(this));
        }

        // Instantiate Doodads
        for (Doodad doodad : baseBattleground.doodads.values()){
            DoodadInstance instance = doodad.createInstance(this);
            if (instance == null)
                continue;
            doodads.put(doodad.getId(), instance);
        }

        // Create Scoreboard
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective sb = scoreboard.registerNewObjective("scoreboard", "dummy", name);
        sb.setDisplaySlot(DisplaySlot.SIDEBAR);
        sb.getScore(ChatColor.RED + "Red Team").setScore(0);
        sb.getScore(ChatColor.BLUE + "Blue Team").setScore(0);

        scoreboard.registerNewTeam("red");
        scoreboard.registerNewTeam("blue");

        scoreboard.getTeam("red").setColor(ChatColor.RED);
        scoreboard.getTeam("red").setCanSeeFriendlyInvisibles(true);
        scoreboard.getTeam("blue").setColor(ChatColor.BLUE);
        scoreboard.getTeam("blue").setCanSeeFriendlyInvisibles(true);

        // Create Main Timer Bar
        timerBar = new ProgressBar("", BarColor.RED, BarStyle.SEGMENTED_20);

        setStatus(BattlegroundStatus.INIT);

        BgManager.registerListener(this);
        Bukkit.getPluginManager().callEvent(new InstanceLoadEvent(this));

        setStatus(BattlegroundStatus.STARTING);
    }

    @Override
    public String getBattlegroundId() {
        return battlegroundId;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public TeamInstance getTeam(TeamColor teamColor) {
        return teams.get(teamColor);
    }

    @Override
    public void addPlayer(Player player) {
        TeamInstance playerTeam = null;
        for (TeamInstance teamInstance : teams.values()){
            if (playerTeam == null || teamInstance.getSize() < playerTeam.getSize())
                playerTeam = teamInstance;
        }

        broadcast(player.getName() + " has joined the battle!", playerTeam.getColor());

        playerTeam.addPlayer(player);

        PlayerData playerData = new PlayerDataBase(player, playerTeam.getColor());
        this.playerData.put(player.getUniqueId(), playerData);

        // Apply Scoreboard & Timer Bar
        player.setScoreboard(scoreboard);
        scoreboard.getTeam(playerData.getTeam().toString().toLowerCase()).addEntry(player.getName());
        timerBar.addPlayer(player);

        // Fix Player Inventory
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW));

        // Fix Player Health, GameMode, & Potion Effects
        player.setHealth(20);
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);

        player.teleport(playerTeam.getSpawnLocation());

        player.sendMessage(playerTeam.getColor().chatColor + ">> You are on the " + playerData.getTeam().toString() + " Team!");
        player.sendTitle(playerTeam.getColor().chatColor + "You are on the " + playerData.getTeam().toString() + " team!", "", 10, 70, 20);

        Bukkit.getPluginManager().callEvent(new PlayerJoinInstanceEvent(player, this));
    }

    @Override
    public void removePlayer(Player player) {
        // Check if player is part of battleground
        if (!isPlayerInBattleground(player))
            return;

        // Call Player Leave Instance Event
        Bukkit.getPluginManager().callEvent(new PlayerLeaveInstanceEvent(player, this));

        PlayerData playerData = this.playerData.get(player.getUniqueId());
        this.playerData.remove(player.getUniqueId());

        // Remove scoreboard & timer bar
        scoreboard.getTeam(playerData.getTeam().toString().toLowerCase()).removeEntry(player.getName());
        playerData.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        timerBar.removePlayer(player);

        // Fix Inventory
        player.getInventory().clear();

        // Fix Player Health, GameMode, & Potion Effects
        player.setHealth(20);
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);

        // Teleport to Main World spawn location
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

        if (this.playerData.size() <= 0)
            close();
    }

    @Override
    public boolean isPlayerInBattleground(Player player) {
        return player != null && playerData.containsKey(player.getUniqueId());
    }

    @Override
    public int getPlayerCount() {
        return playerData.size();
    }

    @Override
    public Location getPlayerSpawnLocation(Player player) {
        if (!playerData.containsKey(player.getUniqueId()))
            return null;

        PlayerData playerData = this.playerData.get(player.getUniqueId());

        if (status != BattlegroundStatus.IN_PROGRESS || !playerData.isDead())
            return teams.get(playerData.getTeam()).getSpawnLocation();

        Location nearestLocation = null;
        double distance = Double.MAX_VALUE;
        for (DoodadInstance doodadInstance : doodads.values()){
            if (doodadInstance instanceof DoodadGraveyardInstance){
                DoodadGraveyardInstance graveyard = (DoodadGraveyardInstance) doodadInstance;

                if (graveyard.getLocation() == null || graveyard.getTeamColor() != playerData.getTeam())
                    continue;

                if (graveyard.canPlayerSpawn(player)) {
                    double currentDistance = graveyard.getLocation().distance(playerData.getDeathLocation());
                    if (nearestLocation == null || currentDistance < distance){
                        nearestLocation = graveyard.getLocation();
                        distance = currentDistance;
                    }
                }
            }
        }

        return nearestLocation != null ? nearestLocation : teams.get(playerData.getTeam()).getSpawnLocation();
    }

    @Override
    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public PlayerData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    @Override
    public PlayerData[] getPlayerData() {
        return playerData.values().toArray(new PlayerData[0]);
    }

    @Override
    public BattlegroundStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(BattlegroundStatus status) {
        Bukkit.getPluginManager().callEvent(new InstanceChangeStatusEvent(this, status));

        timerBar.setTitle(status.text);

        this.status = status;
        switch (status) {
            case INIT:
                timer = 0;
                timerBar.setTitleFlag(ProgressBar.TitleFlag.NONE);
                timerBar.setMaxValue(0);
                break;
            case STARTING:
                timer = 1800;
                timerBar.setTitleFlag(ProgressBar.TitleFlag.TIME);
                timerBar.setMaxValue(90);
                break;
            case IN_PROGRESS:
                for (PlayerData playerData : this.playerData.values()){
                    if (playerData.getPlayer() == null) continue;
                    playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Config.BG_READY_SOUND,
                            Config.BG_READY_SOUND_VOLUME, Config.BG_READY_SOUND_PITCH);
                }
                timer = battleLength*20;
                timerBar.setTitleFlag(ProgressBar.TitleFlag.TIME);
                timerBar.setMaxValue(battleLength);
                break;
            case ENDING:
                for (PlayerData playerData : this.playerData.values()){
                    if (playerData.getPlayer() == null) continue;
                    playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Config.BG_READY_SOUND,
                            Config.BG_READY_SOUND_VOLUME, Config.BG_READY_SOUND_PITCH);
                }

                int redScore = getTeam(TeamColor.RED).getScore();
                int blueScore = getTeam(TeamColor.BLUE).getScore();
                if (redScore > blueScore){
                    broadcast("The Red team has won!");
                } else if (blueScore > redScore) {
                    broadcast("The Blue team has won!");
                } else {
                    broadcast("Both teams have tied!");
                }

                broadcast("Use [/leave] to leave the battleground");

                timer = 1200;
                timerBar.setTitleFlag(ProgressBar.TitleFlag.TIME);
                timerBar.setMaxValue(60);
                break;
            case CLOSED:
                timer = 0;
                timerBar.setMaxValue(0);
                close();
        }
    }

    @Override
    public void broadcast(String msg) {
        playerData.values().forEach(playerData ->
            playerData.getPlayer().sendMessage(ChatColor.YELLOW + ">> " + msg)
        );
    }

    @Override
    public void broadcast(String msg, TeamColor team) {
        playerData.values().forEach(playerData -> {
            if (playerData.getTeam() == team)
                playerData.getPlayer().sendMessage(ChatColor.YELLOW + ">> " + msg);
        });
    }

    @Override
    public void close() {
        List<PlayerData> players = new ArrayList<>(playerData.values());

        // Remove players from battleground
        for (PlayerData playerData : players){
            if (playerData.getPlayer() == null)
                continue;
            removePlayer(playerData.getPlayer());
            playerData.getPlayer().sendMessage(ChatColor.YELLOW + ">> Battleground closed! Teleported to spawn!");
        }

        // Destroy & remove all doodads
        Iterator<DoodadInstance> iterator = doodads.values().iterator();
        while (iterator.hasNext()){
            iterator.next().destroy();
            iterator.remove();
        }

        BgManager.unregisterListener(this);

        WorldManager.unloadWorld(FileDirectory.WORLD_INSTANCE, instanceId, false);
        WorldManager.deleteWorld(FileDirectory.WORLD_INSTANCE, instanceId);
    }

    @Override
    public void tick() {
        if (timer > 0)
            timer--;

        timerBar.setCurrentValue((timer + 10)/20);

        if (getStatus() == BattlegroundStatus.INIT){
            setStatus(BattlegroundStatus.STARTING);
        }

        if (getStatus() == BattlegroundStatus.STARTING){
            if (timer <= 0) {
                notEnoughPlayersTimer = 6000;
                setStatus(BattlegroundStatus.IN_PROGRESS);
            }
        }

        if (getStatus() == BattlegroundStatus.IN_PROGRESS){
            Objective sb = scoreboard.getObjective("scoreboard");
            sb.getScore(ChatColor.RED + "Red Team").setScore(teams.get(TeamColor.RED).getScore());
            sb.getScore(ChatColor.BLUE + "Blue Team").setScore(teams.get(TeamColor.BLUE).getScore());

            if (playerData.size() < teamSize*2){
                if (notEnoughPlayersTimer%1200 == 0)
                    broadcast("Not enough players! Battle ending in " + notEnoughPlayersTimer/1200 + " minutes.");
                notEnoughPlayersTimer--;
            } else
                notEnoughPlayersTimer = 6000;

            if (timer <= 0 || notEnoughPlayersTimer <= 0
                    || teams.get(TeamColor.RED).getScore() >=  winScore
                    || teams.get(TeamColor.BLUE).getScore() >= winScore){

                setStatus(BattlegroundStatus.ENDING);
            }
        }

        if (getStatus() == BattlegroundStatus.ENDING){
            if (timer <= 0){
                setStatus(BattlegroundStatus.CLOSED);
            }
        }

        for (PlayerData playerData : this.playerData.values()){
            if (playerData.getPlayer() == null)
                continue;

            if (playerData.getPlayer().getWorld() != this.getWorld())
                playerData.getPlayer().teleport(getPlayerSpawnLocation(playerData.getPlayer()));

            if (playerData.isDead()){
                playerData.getPlayer().addPotionEffect(
                        new PotionEffect(PotionEffectType.INVISIBILITY, 10, 0));
                playerData.getPlayer().addPotionEffect(
                        new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
            }
        }

        doodads.forEach((k, v) -> v.tick());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        if (event.getBlock().getWorld() == getWorld() && isPlayerInBattleground(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (event.getBlock().getWorld() == getWorld() && isPlayerInBattleground(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        if (event.getEntity() instanceof Player && isPlayerInBattleground((Player) event.getEntity())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity().getWorld() != getWorld())
            return;

        Player damager = null;
        Player player = null;
        if (event.getDamager() instanceof Player){
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow){
            if (( (Arrow) event.getDamager()).getShooter() instanceof Player){
                damager = (Player) ((Arrow) event.getDamager()).getShooter();
            }
        }

        if (event.getEntity() instanceof Player){
            player = (Player) event.getEntity();
        }

        if (damager == null || player == null)
            return;

        if (!isPlayerInBattleground(damager) || !isPlayerInBattleground(player))
            return;

        if (status != BattlegroundStatus.IN_PROGRESS
                || getPlayerData(player).isDead()
                || getPlayerData(damager).isDead()
                || (getPlayerData(player).getTeam() == getPlayerData(damager).getTeam())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event){
        if (isPlayerInBattleground(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if (isPlayerInBattleground(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if (isPlayerInBattleground(event.getPlayer())){
            event.getPlayer().setScoreboard(scoreboard);
            timerBar.addPlayer(event.getPlayer());

            if (event.getPlayer().getWorld() != this.getWorld()){
                event.getPlayer().teleport(getPlayerSpawnLocation(event.getPlayer()));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if (!isPlayerInBattleground(event.getEntity()))
            return;

        PlayerData playerData = getPlayerData(event.getEntity());

        if (status == BattlegroundStatus.IN_PROGRESS) {
            playerData.setDeathLocation(event.getEntity().getLocation());
            playerData.setDead(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if (!isPlayerInBattleground(event.getPlayer()))
            return;

        event.setRespawnLocation(getPlayerSpawnLocation(event.getPlayer()));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event){
        event.getWorld().getName();
    }
}
