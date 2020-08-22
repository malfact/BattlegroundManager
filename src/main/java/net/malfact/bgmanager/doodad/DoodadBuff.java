package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class DoodadBuff extends DoodadRadialField {

    protected PotionEffectType effectType;
    protected int effectDuration = 0;
    protected int effectStrength = 0;
    protected int respawnTime = 0;
    protected Material displayMaterial = Material.AIR;

    protected ArmorStand doodadDisplay;

    public DoodadBuff(String id, Battleground parent) {
        super(id, parent);
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public void setEffectType(PotionEffectType effectType) {
        this.effectType = effectType;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public void setEffectDuration(int effectDuration) {
        this.effectDuration = effectDuration;
    }

    public int getEffectStrength() {
        return effectStrength;
    }

    public void setEffectStrength(int effectStrength) {
        this.effectStrength = effectStrength;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public void setDisplayMaterial(Material displayMaterial) {
        if (displayMaterial == null) displayMaterial = Material.AIR;
        this.displayMaterial = displayMaterial;
        if (doodadDisplay != null && !doodadDisplay.isDead())
            doodadDisplay.getEquipment().setHelmet(new ItemStack(displayMaterial));
    }

    @Override
    public void destroy() {
        super.destroy();
        if (doodadDisplay != null){
            doodadDisplay.remove();
            doodadDisplay = null;
        }
    }

    private int timer = 0;

    @Override
    public void debugTick() {
        super.debugTick();
        if (!debug) {
            debug = parent.getDebug();
            return;
        }

        if (location != null) {
            if (doodadDisplay == null || doodadDisplay.isDead()) {
                doodadDisplay = (ArmorStand) parent.getWorld().spawnEntity(getLocation().add(0, -1.75, 0),
                        EntityType.ARMOR_STAND);
                doodadDisplay.setCanPickupItems(false);
                doodadDisplay.setGravity(false);
                doodadDisplay.setInvulnerable(true);
                doodadDisplay.setVisible(false);
                doodadDisplay.setBasePlate(false);
                doodadDisplay.getEquipment().setHelmet(new ItemStack(displayMaterial));
            }

            timer = timer > 359 ? 0 : timer + 1;

            Location loc = getLocation().add(0, -1.75, 0);
            loc.setYaw(timer);
            doodadDisplay.teleport(loc);
        }
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadBuffInstance(battlegroundInstance, this);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        if (effectType != null) {
            tag.putString("effectType", effectType.getName());
            tag.putInt("effectDuration", effectDuration);
            tag.putInt("effectStrength", effectStrength);
        }
        tag.putInt("respawnTime", respawnTime);
        tag.putString("displayMaterial", displayMaterial.toString());
        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        if (tag.containsKey("effectType")){
            effectType = PotionEffectType.getByName(tag.getString("effectType"));
            effectDuration = tag.getInt("effectDuration");
            effectStrength = tag.getInt("effectStrength");
        }
        respawnTime = tag.getInt("respawnTime");
        setDisplayMaterial(Material.valueOf(tag.getString("displayMaterial")));
        return super.readNBT(tag);
    }

    @EditCommand(cmd = "setRespawnTime")
    public void setRespawnTime(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Respawn Time is set to " + ChatColor.AQUA + getRespawnTime());
            return;
        }

        try {
            int value = Math.abs(Integer.parseInt(args[0]));
            player.sendMessage("<" + id + "> Respawn Time set to " + ChatColor.AQUA + value);
            setRespawnTime(value);
        } catch (NumberFormatException ignored){}
    }

    @EditCommand(cmd = "setEffectDuration")
    public void setEffectDuration(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Effect Duration is set to " + ChatColor.AQUA + getEffectDuration());
            return;
        }

        try {
            int value = Math.min(Math.abs(Integer.parseInt(args[0])), 999999);
            player.sendMessage("<" + id + "> Effect Duration set to " + ChatColor.AQUA + value);
            setEffectDuration(value);
        } catch (NumberFormatException ignored){}
    }

    @EditCommand(cmd = "setEffectStrength")
    public void setEffectStrength(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Effect Strength is set to " + ChatColor.AQUA + getEffectStrength());
            return;
        }

        try {
            int value = Math.min(Math.abs(Integer.parseInt(args[0])), 255);
            player.sendMessage("<" + id + "> Effect Strength set to " + ChatColor.AQUA + value);
            setEffectStrength(value);
        } catch (NumberFormatException ignored){}
    }

    @EditCommand(cmd ="setEffectType")
    public void setEffectType(Player player, String[] args){
        if (args.length == 0) {
            StringBuilder values = new StringBuilder();
            for (PotionEffectType effectType : PotionEffectType.values())
                values.append(effectType.getName()).append("; ");

            String name = effectType == null ? "NULL" : effectType.getName();
            player.sendMessage("<" + id + "> Effect Type set to " + ChatColor.AQUA + name);
            player.sendMessage("Acceptable Values: " + ChatColor.AQUA + values.toString());

            return;
        }

        PotionEffectType effectType = PotionEffectType.getByName(args[0]);
        if (effectType != null) {
            player.sendMessage("<" + id + "> Effect Type set to " + effectType.getName());
            setEffectType(effectType);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid Value: " + args[0]);
        }
    }

    @Override
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        super.onPlayerInteractAtEntity(event);

        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && event.getPlayer().isSneaking()){
            event.setCancelled(true);
            String effectType = this.effectType != null ? this.effectType.getName() : "NULL";
            event.getPlayer().sendMessage(ChatColor.GOLD + "Effect Type is set to " + ChatColor.AQUA + effectType);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Effect Duration is set to " + ChatColor.AQUA + effectDuration);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Effect Strength is set to " + ChatColor.AQUA + effectStrength);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Respawn Time is set to " + ChatColor.AQUA + respawnTime);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Display Material is set to " + ChatColor.AQUA + displayMaterial.toString());
        }

        if (event.getRightClicked() == doodadInteractor && !event.getPlayer().isSneaking()) {
            event.setCancelled(true);
            setDisplayMaterial(event.getPlayer().getInventory().getItemInMainHand().getType());
            event.getPlayer().sendMessage(ChatColor.GOLD + "Display Material set to " + ChatColor.AQUA + displayMaterial.toString());
        }
    }
}
