package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.doodad.instance.DoodadFlagSpawnInstance;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class DoodadFlagSpawn extends DoodadPhysicalOwnableBase {

    protected int respawnTime = 30;

    protected ArmorStand flagStand;

    public DoodadFlagSpawn(String id, Battleground parent) {
        super(id, parent);
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setTeamColor(TeamColor teamColor){
        super.setTeamColor(teamColor);

        refreshBanner();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (flagStand != null){
            flagStand.remove();
            flagStand = null;
        }
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);

        if (flagStand != null)
            flagStand.teleport(getLocation().add(0.0, -1.75, 0.0));
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadFlagSpawnInstance(battlegroundInstance, this);
    }

    @Override
    public void debugTick() {
        super.debugTick();

        if (location == null)
            return;

        if (flagStand == null || flagStand.isDead()) {
            Location l = getLocation();

            ArmorStand armorStand = (ArmorStand) battleground.getWorld()
                    .spawnEntity(l.add(0.0, -1.75, 0.0), EntityType.ARMOR_STAND);
            armorStand.setCustomName("");
            armorStand.setCanPickupItems(false);
            armorStand.setGravity(false);
            armorStand.setBasePlate(false);
            armorStand.setInvulnerable(true);
            armorStand.setVisible(false);

            flagStand = armorStand;

            refreshBanner();
        }
    }

    private void refreshBanner(){
        if (flagStand == null) return;

        EntityEquipment equipment = flagStand.getEquipment();
        if (teamColor == TeamColor.RED)
            equipment.setHelmet(new ItemStack(Material.RED_BANNER));
        else if (teamColor == TeamColor.BLUE)
            equipment.setHelmet(new ItemStack(Material.BLUE_BANNER));
        else if (teamColor == TeamColor.GREEN)
            equipment.setHelmet(new ItemStack(Material.GREEN_BANNER));
        else
            equipment.setHelmet(new ItemStack(Material.WHITE_BANNER));
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putInt("respawnTime", respawnTime);
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        respawnTime = tag.getInt("respawnTime");
        return super.readNBT(tag);
    }

    @EditCommand(cmd = "setRespawnTime")
    public void setRespawnTime(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Respawn Time is set to " + ChatColor.AQUA + respawnTime);
            return;
        }

        try {
            int value = Math.abs(Integer.parseInt(args[0]));
            player.sendMessage("<" + id + "> Respawn Time set to " + ChatColor.AQUA + value);
            setRespawnTime(value);
        } catch (NumberFormatException ignored){}
    }

    @Override
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        super.onPlayerInteractAtEntity(event);

        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && event.getPlayer().isSneaking()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Respawn Time is set to " + ChatColor.AQUA + respawnTime);
        }
    }
}

