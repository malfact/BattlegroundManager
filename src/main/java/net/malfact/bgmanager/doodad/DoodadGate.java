package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.doodad.instance.DoodadGateInstance;
import net.malfact.bgmanager.util.Config;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DoodadGate extends DoodadPhysicalBase {

    protected final Set<Location> gateBlocks;

    public DoodadGate(String id, Battleground parent) {
        super(id, parent);
        gateBlocks = new HashSet<>();
    }

    public Location[] getGateBlocks() {
        Location[] gateBlocks = new Location[this.gateBlocks.size()];
        int count = 0;
        for (Location location : this.gateBlocks){
            gateBlocks[count] = location.clone();
            count++;
        }
        return gateBlocks;
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        if (!debug){
            playersEditing.clear();
        }
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadGateInstance(battlegroundInstance, this);
    }

    @Override
    public void debugTick() {
        super.debugTick();

        for (Location blockLocation : gateBlocks){
            Location l = blockLocation.clone();
            l.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    l.add(0.5,0.5,0.5),
                    1,
                    new Particle.DustOptions(Color.YELLOW, 0.5f)
            );
        }
    }

    public void addGateBlock(int x, int y, int z){
        gateBlocks.add(new Location(battleground.getWorld(), x, y, z));
    }

    public void removeGateBlock(int x, int y, int z){
        gateBlocks.remove(new Location(battleground.getWorld(), x, y, z));
    }

    public boolean hasGateBlock(int x, int y, int z){
        return gateBlocks.contains(new Location(battleground.getWorld(), x, y, z));
    }

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        ListTag<CompoundTag> blockList = tag.getListTag("blocks").asCompoundTagList();

        for (CompoundTag locationTag : blockList) {
            addGateBlock(locationTag.getInt("x"), locationTag.getInt("y"), locationTag.getInt("z"));
        }

        return super.readNBT(tag);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        ListTag<CompoundTag> blockList = new ListTag<>(CompoundTag.class);

        for (Location blockLocation : gateBlocks){
            CompoundTag locationTag = new CompoundTag();

            locationTag.putInt("x", blockLocation.getBlockX());
            locationTag.putInt("y", blockLocation.getBlockY());
            locationTag.putInt("z", blockLocation.getBlockZ());

            blockList.add(locationTag);
        }

        tag.put("blocks", blockList);

        return super.writeNBT(tag);
    }

    private final Set<UUID> playersEditing = new HashSet<>();

    @Override
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        super.onPlayerInteractAtEntity(event);

        if (!debug) return;

        if (event.getRightClicked() == doodadInteractor && !event.getPlayer().isSneaking()){
            if (!playersEditing.contains(event.getPlayer().getUniqueId())) {

                playersEditing.add(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(ChatColor.GOLD + "You are now editing <" + id + ">");
                event.setCancelled(true);
                return;
            }
        }

        if (playersEditing.contains(event.getPlayer().getUniqueId())){
            playersEditing.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer editing <" + id + ">");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!playersEditing.contains(event.getPlayer().getUniqueId())
                || (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND))
                || event.getClickedBlock() == null)
            return;

        event.setCancelled(true);

        if (!event.getClickedBlock().getBlockData().getMaterial().equals(Material.IRON_BARS)){
            event.getPlayer().sendMessage(ChatColor.RED + "You can only select iron bars as a gate!");
            return;
        }

        Block block = event.getClickedBlock();
        if (hasGateBlock(block.getX(), block.getY(), block.getZ())) {
            removeGateBlock(block.getX(), block.getY(), block.getZ());
            event.getPlayer().sendMessage(
                    String.format(
                            "%6$s<%1$s,%2$s,%3$s>%5$s removed from %6$s<%4$s>",
                            block.getX(), block.getY(), block.getZ(), id, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT
                    )
            );

        } else {
            addGateBlock(block.getX(), block.getY(), block.getZ());
            event.getPlayer().sendMessage(
                    String.format(
                            "%6$s<%1$s,%2$s,%3$s>%5$s added to %6$s<%4$s>",
                            block.getX(), block.getY(), block.getZ(), id, Config.COMMAND_COLOR, Config.COMMAND_HIGHLIGHT
                    )
            );
        }
    }
}
