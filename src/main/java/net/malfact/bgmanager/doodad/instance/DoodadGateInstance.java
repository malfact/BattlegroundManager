package net.malfact.bgmanager.doodad.instance;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import net.malfact.bgmanager.doodad.DoodadGate;
import net.malfact.bgmanager.event.BattlegroundChangeStatusEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DoodadGateInstance extends DoodadPhysicalInstance implements Listener {

    protected final Set<Location> gateBlocks;

    public DoodadGateInstance(BattlegroundInstance battlegroundInstance, DoodadGate doodad) {
        super(battlegroundInstance, doodad);
        gateBlocks = new HashSet<>();
        gateBlocks.addAll(Arrays.asList(doodad.getGateBlocks()));

        BgManager.registerListener(this);
    }

    @Override
    public void destroy() {
        BgManager.unregisterListener(this);
    }

    @Override
    public void tick() {

    }

    @EventHandler
    public void onBattlegroundChangeStatus(BattlegroundChangeStatusEvent event){
        if (event.getBattleground() == this.instance && event.getStatus() == BattlegroundStatus.IN_PROGRESS){
            for (Location loc : gateBlocks){
                loc.setWorld(instance.getWorld());
                loc.getBlock().setType(Material.AIR);
            }
        }
    }
}
