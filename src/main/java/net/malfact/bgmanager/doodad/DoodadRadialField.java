package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.doodad.DoodadType;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class DoodadRadialField extends DoodadBase {
    protected double radius = 1.0;
    protected Color color = Color.YELLOW;

    public DoodadRadialField(String id, Battleground parent) {
        super(id, parent);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadRadialFieldInstance(battlegroundInstance, this);
    }

    @Override
    public void debugTick() {
        super.debugTick();

        if (location == null || radius == 0)
            return;

        double inc = (Math.PI/(radius*10));

        for (double i = 0; i < Math.PI*2; i+=inc){
            Location l = getLocation();
            l.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    l.add(radius * Math.sin(i), 0.0, radius * Math.cos(i)),
                    1,
                    new Particle.DustOptions(color, 0.5f)
            );
        }
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        tag.putDouble("radius", radius);

        return super.writeNBT(tag);
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        radius = tag.getDouble("radius");

        return super.readNBT(tag);
    }

    @EditCommand(cmd = "setradius", args = {double.class})
    public void setRadius(Player player, String... args){
        if (args.length == 0) {
            player.sendMessage("<" + id + "> Radius is set to " + ChatColor.AQUA + getRadius());
            return;
        }

        try {
            double radius = Math.abs(Double.parseDouble(args[0]));
            setRadius(radius);
            player.sendMessage("<" + id + "> Radius set to " + ChatColor.AQUA + radius);
        } catch (NumberFormatException e){}
    }

    @Override
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        super.onPlayerInteractAtEntity(event);

        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && event.getPlayer().isSneaking()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Radius is set to " + ChatColor.AQUA + getRadius());
        }
    }
}
