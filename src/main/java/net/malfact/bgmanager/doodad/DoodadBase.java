package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadPhysical;
import net.malfact.bgmanager.api.doodad.DoodadType;
import net.malfact.bgmanager.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public abstract class DoodadBase implements DoodadPhysical, Listener {
    public final String id;
    public final Battleground parent;

    protected Location location;
    protected boolean debug = false;

    protected ArmorStand doodadInteractor;

    public DoodadBase(String id, Battleground parent){
        this.id = id;
        this.parent = parent;

        BgManager.registerListener(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setLocation(Location location) {
        location.setWorld(parent.getWorld());
        location.setX(location.getBlockX()+0.5);
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ()+0.5);
        float yaw = location.getYaw() + 22.5F;
        yaw = (float) Math.floor(yaw/45f);
        yaw = yaw * 45f;
        location.setYaw(yaw);
        location.setPitch(0F);
        this.location = location;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (!debug)
            destroy();
    }

    @Override
    public Location getLocation() {
        if (location == null)
            return null;

        Location l = location.clone();
        l.setWorld(parent.getWorld());

        return l;
    }

    @Override
    public void destroy() {
        BgManager.unregisterListener(this);
        despawnInteractor();
    }

    @Override
    public void debugTick() {
        if (location == null)
            return;

        if (!isInteractorSpawned())
            spawnInteractor();

        doodadInteractor.teleport(getLocation());
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag){
        tag.putString("doodad_type", DoodadType.getByClass(this.getClass()).getName());

        Location location = getLocation();

        if (location != null) {
            CompoundTag locationTag = new CompoundTag();
            locationTag.putDouble("x", location.getX());
            locationTag.putDouble("y", location.getY());
            locationTag.putDouble("z", location.getZ());
            locationTag.putFloat("yaw", location.getYaw());
            tag.put("location", locationTag);
        }

        return tag;
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag){
        CompoundTag locationTag = tag.getCompoundTag("location");
        if (locationTag != null) {
            setLocation(
                    new Location(
                            null,
                            locationTag.getDouble("x"),
                            locationTag.getDouble("y"),
                            locationTag.getDouble("z"),
                            locationTag.getFloat("yaw"),
                            0
                    ));
        }

        return tag;
    }

    protected void spawnInteractor(){
        despawnInteractor();

        doodadInteractor = (ArmorStand) parent.getWorld().spawnEntity(getLocation(), EntityType.ARMOR_STAND);
        doodadInteractor.setCustomName("<" + id + ">");
        doodadInteractor.setCustomNameVisible(true);
        doodadInteractor.setCanPickupItems(false);
        doodadInteractor.setGravity(false);
        doodadInteractor.setInvulnerable(true);
        doodadInteractor.setVisible(true);
        doodadInteractor.setBasePlate(false);
    }

    protected void despawnInteractor(){
        if (doodadInteractor != null){
            doodadInteractor.remove();
            doodadInteractor = null;
        }
    }

    protected boolean isInteractorSpawned(){
        return doodadInteractor != null && !doodadInteractor.isDead();
    }

    @EditCommand(cmd ="setLocation")
    public void setLocation(Player player, String... args){
        player.sendMessage("<" + id + "> Location set to your current location");
        setLocation(player.getLocation().clone());
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && event.getPlayer().isSneaking()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Location is set to " + ChatColor.AQUA
                    + "{"
                    + location.getX() + ", "
                    + location.getY() + ", "
                    + location.getZ() + "| yaw = "
                    + location.getYaw() + "}");
        }
    }

}
