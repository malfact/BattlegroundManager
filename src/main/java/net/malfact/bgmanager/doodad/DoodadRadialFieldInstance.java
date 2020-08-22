package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class DoodadRadialFieldInstance extends DoodadPhysicalBaseInstance {

    protected final double radius;
    protected final Color color;
    protected boolean active = true;

    protected DoodadRadialFieldInstance(BattlegroundInstance battlegroundInstance, DoodadRadialField doodad) {
        super(battlegroundInstance, doodad);
        this.radius = doodad.radius;
        this.color = doodad.color;
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
