package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.BattlegroundStatus;
import net.malfact.bgmanager.api.battleground.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DoodadBuffInstance extends DoodadRadialFieldInstance implements Listener {

    protected final PotionEffectType effectType;
    protected final int effectDuration;
    protected final int effectStrength;
    protected final int respawnTime;

    protected boolean buffSpawned = false;
    protected int respawnTimer = 0;
    protected int displayTimer = 0;

    protected Material displayMaterial;
    protected ArmorStand doodadDisplay;

    protected DoodadBuffInstance(BattlegroundInstance battlegroundInstance, DoodadBuff doodad) {
        super(battlegroundInstance, doodad);
        this.effectType = doodad.effectType;
        this.effectDuration = doodad.effectDuration;
        this.effectStrength = doodad.effectStrength;
        this.respawnTime = doodad.respawnTime;

        this.displayMaterial = doodad.displayMaterial;

        BgManager.registerListener(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        BgManager.unregisterListener(this);
    }

    @Override
    public void tick() {
        super.tick();
        super.active = buffSpawned;

        if (effectType == null)
            return;

        if (battlegroundInstance.getStatus() == BattlegroundStatus.IN_PROGRESS){
            if (!buffSpawned){
                if (isDisplaySpawned())
                    despawnDisplay();

                if (respawnTimer > 0){
                    respawnTimer--;
                } else {
                    respawnTimer = respawnTime*20;
                    buffSpawned = true;
                }
            } else {
                if (!isDisplaySpawned())
                    spawnDisplay();

                displayTimer = displayTimer > 359 ? 0 : displayTimer + 1;

                Location loc = location.clone().add(0, -1.75, 0);
                loc.setYaw(displayTimer);
                doodadDisplay.teleport(loc);

                for (PlayerData playerData : battlegroundInstance.getPlayerData()) {
                    if (isPlayerInRadius(playerData.getPlayer())) {
                        playerData.getPlayer().addPotionEffect(
                                new PotionEffect(effectType, effectDuration*20 + 20, effectStrength)
                        );
                        buffSpawned = false;
                        break;
                    }
                }
            }

        }
    }

    protected void spawnDisplay(){
        despawnDisplay();

        doodadDisplay = (ArmorStand) battlegroundInstance.getWorld().spawnEntity(
                location.clone().add(0, -1.75, 0), EntityType.ARMOR_STAND);
        doodadDisplay.setCanPickupItems(false);
        doodadDisplay.setGravity(false);
        doodadDisplay.setInvulnerable(true);
        doodadDisplay.setVisible(false);
        doodadDisplay.setBasePlate(false);
        doodadDisplay.getEquipment().setHelmet(new ItemStack(displayMaterial));
    }

    protected void despawnDisplay(){
        if (doodadDisplay != null){
            doodadDisplay.remove();
            doodadDisplay = null;
        }
    }

    protected boolean isDisplaySpawned(){
        return doodadDisplay != null && !doodadDisplay.isDead();
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        if (event.getRightClicked() == doodadDisplay)
            event.setCancelled(true);
    }
}
