package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import net.malfact.bgmanager.api.battleground.PlayerData;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.doodad.DoodadFlagSpawn;
import net.malfact.bgmanager.event.FlagCaptureEvent;
import net.malfact.bgmanager.event.FlagDespawnEvent;
import net.malfact.bgmanager.event.FlagPickupEvent;
import net.malfact.bgmanager.event.FlagSpawnEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DoodadFlagSpawnInstance extends DoodadPhysicalInstance implements Listener {

    protected final TeamColor teamColor;
    protected final int respawnTime;

    protected ArmorStand flagDisplay;
    protected ArmorStand flagInteractor;

    protected ArmorStand mobileFlagDisplay;
    protected ArmorStand mobileFlagInteractor;

    protected PlayerData flagHolder;

    protected boolean flagSpawned = false;
    protected boolean flagCaptured = false;
    protected boolean flagMobile = false;

    protected int respawnTimer = 0;
    protected int mobileTimer = 0;
    protected int timer = 0;

    public DoodadFlagSpawnInstance(BattlegroundInstance battlegroundInstance, DoodadFlagSpawn doodad) {
        super(battlegroundInstance, doodad);
        this.teamColor = doodad.getTeamColor();
        this.respawnTime = doodad.getRespawnTime();

        BgManager.registerListener(this);
    }

    @Override
    public void destroy() {
        despawnFlag();
        despawnMobileFlag();

        BgManager.unregisterListener(this);
    }

    protected int flagHoldTime = 0;

    @Override
    public void tick() {
        if (instance.getStatus() != BattlegroundStatus.IN_PROGRESS){
            if (flagSpawned)
                despawnFlag();

            respawnTimer = respawnTime*20;
        }

        if (instance.getStatus() == BattlegroundStatus.IN_PROGRESS) {
            if (!flagSpawned && !flagCaptured && !flagMobile && respawnTimer <= 0) {
                instance.broadcast("The " + teamColor.toString() + " Team's flag has spawned!");
                respawnTimer = respawnTime*20;
                spawnFlag();

            } else if (!flagSpawned && !flagCaptured && !flagMobile){
                respawnTimer--;
            }

            if (!flagSpawned && (flagCaptured || flagMobile)){
                flagHoldTime += flagHoldTime >= 3600 ? 0 : 1;
            }

            if (flagHolder != null){
                if (!flagHolder.getPlayer().isOnline()){
                    instance.broadcast("The " + teamColor.chatColor
                            + "Team's flag has been returned to it's base!");

                    flagHolder.setFlag(null, "");
                    flagHolder = null;
                    flagCaptured = false;

                    spawnFlag();
                } else {
                    Location loc = flagHolder.getPlayer().getLocation();

                    timer = timer < 20 ? timer + 1 : 0;
                    double angle = (Math.PI / 10) * timer;

                    getInstance().getWorld().spawnParticle(
                            Particle.REDSTONE,
                            loc.add(0.75 * Math.sin(angle), 2.0, 0.75 * Math.cos(angle)),
                            1,
                            new Particle.DustOptions(teamColor.color, 1f));

                    if (flagHoldTime >= 3600){
                        flagHolder.getPlayer().addPotionEffect(
                                new PotionEffect(PotionEffectType.GLOWING, 10, 1)
                        );
                    }
                }
            }

            if (flagMobile){
                if ((mobileFlagDisplay == null || mobileFlagDisplay.isDead())
                        || (mobileFlagInteractor == null || mobileFlagInteractor.isDead())) {
                    despawnMobileFlag();
                    spawnFlag();
                } else {
                    mobileTimer--;

                    mobileFlagInteractor.setCustomName(mobileTimer/20 + "");

                    if (mobileTimer <= 0){
                        instance.broadcast("The " + teamColor.toString()
                                + "Team's flag has been returned to it's base!");
                        despawnMobileFlag();
                        spawnFlag();
                    }
                }
            }

        }
    }

    private Location getFirstSolidBlock(Location location){
        Location solid = null;
        for (int i = 0; i < 5; i++){
            Location tempLocation = location.clone();
            if (tempLocation.add(0,-i,0).getBlock().getType().isSolid()){
                solid = tempLocation.add(0, 1.0,0);
                break;
            }
        }
        return solid;
    }

    private void spawnFlag(){
        despawnFlag();
        Location loc0 = location.clone();

        flagInteractor = (ArmorStand) instance.getWorld().spawnEntity(loc0, EntityType.ARMOR_STAND);
        flagInteractor.setCustomName("Flag Holder");
        flagInteractor.setCanPickupItems(false);
        flagInteractor.setGravity(false);
        flagInteractor.setBasePlate(false);
        flagInteractor.setInvulnerable(true);
        flagInteractor.setVisible(false);

        Location loc1 = location.clone();

        flagDisplay = (ArmorStand) instance.getWorld().spawnEntity(loc1.add(0.0, -1.75, 0.0),
                EntityType.ARMOR_STAND);
        flagDisplay.setCustomName("");
        flagDisplay.setCanPickupItems(false);
        flagDisplay.setGravity(false);
        flagDisplay.setBasePlate(false);
        flagDisplay.setInvulnerable(true);
        flagDisplay.setVisible(false);

        EntityEquipment equipment = flagDisplay.getEquipment();
        if (teamColor == TeamColor.RED)
            equipment.setHelmet(new ItemStack(Material.RED_BANNER));
        else
            equipment.setHelmet(new ItemStack(Material.BLUE_BANNER));

        flagSpawned = true;
        respawnTimer = respawnTime*20;
        flagHoldTime = 0;

        Bukkit.getPluginManager().callEvent(new FlagSpawnEvent(this.teamColor, this.id, instance, false));
    }

    private void despawnFlag(){

        if (flagDisplay != null){
            flagDisplay.remove();
            flagDisplay = null;
        }

        if (flagInteractor != null){
            flagInteractor.remove();
            flagInteractor = null;
        }

        flagSpawned = false;

        Bukkit.getPluginManager().callEvent(new FlagDespawnEvent(this.teamColor, this.id, instance, false));
    }

    private boolean isFlagDisplaySpawned(){
        return (flagDisplay != null && !flagDisplay.isDead()) && (flagInteractor != null && !flagInteractor.isDead());
    }

    private void spawnMobileFlag(Location location){
        despawnMobileFlag();

        mobileFlagDisplay = (ArmorStand) instance.getWorld()
                .spawnEntity(location.clone().add(0.0, -1.75, 0.0), EntityType.ARMOR_STAND);
        mobileFlagDisplay.setCustomName("Flag Holder");
        mobileFlagDisplay.setCanPickupItems(false);
        mobileFlagDisplay.setGravity(false);
        mobileFlagDisplay.setBasePlate(false);
        mobileFlagDisplay.setInvulnerable(true);
        mobileFlagDisplay.setVisible(false);

        mobileFlagInteractor = (ArmorStand) instance.getWorld()
                .spawnEntity(location.clone(), EntityType.ARMOR_STAND);
        mobileFlagInteractor.setCustomName("");
        mobileFlagInteractor.setCustomNameVisible(true);
        mobileFlagInteractor.setCanPickupItems(false);
        mobileFlagInteractor.setGravity(false);
        mobileFlagInteractor.setBasePlate(false);
        mobileFlagInteractor.setInvulnerable(true);
        mobileFlagInteractor.setVisible(false);

        EntityEquipment equipment = mobileFlagDisplay.getEquipment();
        if (teamColor == TeamColor.RED)
            equipment.setHelmet(new ItemStack(Material.RED_BANNER));
        else
            equipment.setHelmet(new ItemStack(Material.BLUE_BANNER));

        mobileTimer = 200;
        flagMobile = true;

        Bukkit.getPluginManager().callEvent(new FlagSpawnEvent(this.teamColor, this.id, instance,true));
    }

    private void despawnMobileFlag(){
        if (mobileFlagInteractor != null){
            mobileFlagInteractor.remove();
            mobileFlagInteractor = null;
        }

        if (mobileFlagDisplay != null){
            mobileFlagDisplay.remove();;
            mobileFlagDisplay = null;
        }

        flagMobile = false;

        Bukkit.getPluginManager().callEvent(new FlagDespawnEvent(this.teamColor, this.id, instance, true));
    }

    private boolean isFlagMobileDisplaySpawned(){
        return (mobileFlagDisplay != null && !mobileFlagDisplay.isDead())
                && (mobileFlagInteractor != null && !mobileFlagInteractor.isDead());
    }

    @EventHandler
    public void onFlagCapture(FlagCaptureEvent event){
        respawnTimer = respawnTime*20;
        flagHolder = null;
        flagCaptured = false;
        despawnFlag();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (!instance.isPlayerInBattleground(event.getPlayer()))
            return;

        if (flagHolder == null || event.getHand() == EquipmentSlot.OFF_HAND
                || event.getPlayer() != flagHolder.getPlayer())
            return;

        if (event.getPlayer().isSneaking()
                && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)){

            instance.broadcast(flagHolder.getTeam().chatColor
                    + flagHolder.getPlayer().getName() + " has dropped the " + teamColor + " Team's flag!");

            flagHolder.setFlag(null, "");
            flagHolder = null;
            flagCaptured = false;
            Location dropLocation = getFirstSolidBlock(event.getPlayer().getLocation());

            if (dropLocation != null)
                spawnMobileFlag(dropLocation);
            else {
                instance.broadcast("The " + teamColor.chatColor
                        + "Team's flag has been returned to it's base!");
                spawnFlag();
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if (!instance.isPlayerInBattleground(event.getPlayer()))
            return;

        if (instance.getStatus() != BattlegroundStatus.IN_PROGRESS)
            return;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        PlayerData playerData = instance.getPlayerData(player);

        if (playerData.isDead())
            return;

        if (entity == flagInteractor || entity == flagDisplay){
            event.setCancelled(true);

            if (playerData.getTeam() != teamColor) {
                flagCaptured = true;
                playerData.setFlag(teamColor, id);
                flagHolder = playerData;
                despawnFlag();

                instance.broadcast(playerData.getTeam().chatColor  + player.getDisplayName()
                        + " has picked up the " + teamColor.toString() + " Team's flag!");
                event.getPlayer().sendMessage(ChatColor.GOLD + ">> [Sneak] + [Left Click] to drop the flag!");

                Bukkit.getPluginManager().callEvent(new FlagPickupEvent(this.teamColor, id, instance, player, playerData.getTeam()));
            }
        } else if (entity == mobileFlagInteractor || entity == mobileFlagDisplay){
            event.setCancelled(true);

            if (playerData.getTeam() == teamColor){
                despawnMobileFlag();
                spawnFlag();

                instance.broadcast(playerData.getTeam().chatColor + player.getDisplayName()
                        + " has returned their flag to their base!");

            } else {
                flagCaptured = true;
                playerData.setFlag(teamColor, id);
                flagHolder = playerData;
                despawnMobileFlag();

                instance.broadcast(playerData.getTeam().chatColor +player.getDisplayName()
                        + " has picked up the " + teamColor.toString() + " Team's flag!");
                event.getPlayer().sendMessage(ChatColor.GOLD + ">> [Sneak] + [Left Click] to drop the flag!");

                Bukkit.getPluginManager().callEvent(new FlagPickupEvent(this.teamColor, id, instance, player, playerData.getTeam()));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if (flagHolder == null || !instance.isPlayerInBattleground(event.getEntity()))
            return;

        PlayerData playerData = instance.getPlayerData(event.getEntity());

        if (flagHolder == playerData){
            instance.broadcast(flagHolder.getTeam().chatColor
                    + flagHolder.getPlayer().getName() + " has dropped the " + teamColor + " Team's flag!");

            Location dropLocation = getFirstSolidBlock(flagHolder.getPlayer().getLocation());

            flagHolder.setFlag(null, "");
            flagHolder = null;
            flagCaptured = false;
            if (dropLocation != null){
                spawnMobileFlag(dropLocation);
            } else {
                instance.broadcast("The " + teamColor.chatColor
                        + "Team's flag has been returned to it's base!");
                spawnFlag();
            }
        }
    }
}
