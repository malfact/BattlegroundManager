package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.doodad.DoodadRadialField;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class DoodadRadialFieldInstance extends DoodadPhysicalInstance {

    protected final double radius;

    protected Color color;
    protected boolean active = true;

    public DoodadRadialFieldInstance(BattlegroundInstance battlegroundInstance, DoodadRadialField doodad) {
        super(battlegroundInstance, doodad);
        this.radius = doodad.getRadius();
        this.color = doodad.getColor();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void tick() {
        if (location == null || radius == 0)
            return;

        if (active) {
            double inc = (Math.PI / (radius * 10));

            for (double i = 0; i < Math.PI * 2; i += inc) {
                Location l = location.clone();
                l.getWorld().spawnParticle(
                        Particle.REDSTONE,
                        l.add(radius * Math.sin(i), 0.0, radius * Math.cos(i)),
                        1,
                        new Particle.DustOptions(color, 0.5f)
                );
            }
        }
    }

    public boolean isPlayerInRadius(Player player){
        return player != null && player.getLocation().distance(location) <= radius;
    }
}
