package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import net.malfact.bgmanager.event.BattlegroundChangeStatusEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class DoodadGateInstance extends DoodadBaseInstance implements Listener {

    protected final Set<Location> gateBlocks;

    protected DoodadGateInstance(BattlegroundInstance battlegroundInstance, DoodadGate doodad) {
        super(battlegroundInstance, doodad);
        gateBlocks = new HashSet<>();
        for (Location l : doodad.gateBlocks){
            gateBlocks.add(l.clone());
        }

        BgManager.registerListener(this);
    }

    @Override
    public void destroy() {
        BgManager.unregisterListener(this);
    }

    @EventHandler
    public void onBattlegroundChangeStatus(BattlegroundChangeStatusEvent event){
        if (event.getBattleground() == this.battlegroundInstance && event.getStatus() == BattlegroundStatus.IN_PROGRESS){
            for (Location loc : gateBlocks){
                loc.setWorld(battlegroundInstance.getWorld());
                loc.getBlock().setType(Material.AIR);
            }
        }
    }
}
