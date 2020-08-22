package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.battleground.TeamColor;
import net.malfact.bgmanager.command.edit.EditCommand;
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

public class DoodadFlagSpawn extends DoodadBase {

    protected TeamColor teamColor = TeamColor.RED;
    protected int respawnTime = 30;

    protected ArmorStand flagStand;

    public DoodadFlagSpawn(String id, Battleground parent) {
        super(id, parent);
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (flagStand != null)
            flagStand.remove();
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);

        if (flagStand != null)
            flagStand.teleport(getLocation().add(0.0, -1.75, 0.0));
    }

    public void setTeamColor(TeamColor teamColor){
        this.teamColor = teamColor;

        if (flagStand != null){
            EntityEquipment equipment = flagStand.getEquipment();
            if (teamColor == TeamColor.RED)
                equipment.setHelmet(new ItemStack(Material.RED_BANNER));
            else
                equipment.setHelmet(new ItemStack(Material.BLUE_BANNER));
        }
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

            ArmorStand armorStand = (ArmorStand) parent.getWorld()
                    .spawnEntity(l.add(0.0, -1.75, 0.0), EntityType.ARMOR_STAND);
            armorStand.setCustomName("");
            armorStand.setCanPickupItems(false);
            armorStand.setGravity(false);
            armorStand.setBasePlate(false);
            armorStand.setInvulnerable(true);
            armorStand.setVisible(false);

            flagStand = armorStand;

            EntityEquipment equipment = flagStand.getEquipment();
            if (teamColor == TeamColor.RED)
                equipment.setHelmet(new ItemStack(Material.RED_BANNER));
            else
                equipment.setHelmet(new ItemStack(Material.BLUE_BANNER));
        }
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putString("color", teamColor.toString());
        tag.putInt("respawnTime", respawnTime);
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        teamColor = TeamColor.valueOf(tag.getString("color"));
        respawnTime = tag.getInt("respawnTime");
        return super.readNBT(tag);
    }

    @EditCommand(cmd ="setTeamColor")
    public void setTeamColor(Player player, String[] args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Team Color is set to " + teamColor.chatColor + teamColor.toString());
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
            event.getPlayer().sendMessage("<" + id + "> Team Color is set to " + teamColor.chatColor + teamColor.toString());
            event.getPlayer().sendMessage(ChatColor.GOLD + "Respawn Time is set to " + ChatColor.AQUA + respawnTime);
        }
    }
}

