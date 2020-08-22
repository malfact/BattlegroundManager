package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.BgManager;
import net.malfact.bgmanager.api.battleground.Battleground;
import net.malfact.bgmanager.api.battleground.BattlegroundInstance;
import net.malfact.bgmanager.api.battleground.TeamColor;
import net.malfact.bgmanager.api.doodad.Doodad;
import net.malfact.bgmanager.api.doodad.DoodadGroup;
import net.malfact.bgmanager.api.doodad.DoodadInstance;
import net.malfact.bgmanager.api.doodad.DoodadOwnable;
import net.malfact.bgmanager.command.edit.EditCommand;
import net.malfact.bgmanager.doodad.instance.DoodadGroupInstance;
import net.malfact.bgmanager.doodad.instance.DoodadOwnableBase;
import net.malfact.bgmanager.event.BattlegroundLoadEvent;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class DoodadGroupBase extends DoodadOwnableBase implements DoodadGroup, Listener {

    protected Set<String> children = new HashSet<>();

    public DoodadGroupBase(String id, Battleground battleground) {
        super(id, battleground);

        BgManager.registerListener(this);
    }

    protected void updateChildren(){
        for (String doodadId : children){
            DoodadOwnable doodad = getChild(doodadId);
            if (doodad != null) {
                doodad.setTeamColor(teamColor);
                doodad.setOwnerId(this.id);
            }
        }
    }

    @Override
    public void setTeamColor(TeamColor teamColor) {
        super.setTeamColor(teamColor);

        updateChildren();
    }

    @Override
    public void addChild(DoodadOwnable doodad) {
        if (doodad == null) return;

        children.add(doodad.getId());
        doodad.setOwnerId(this.ownerId);
        doodad.setTeamColor(this.teamColor);
    }

    @Override
    public void removeChild(DoodadOwnable doodad) {
        if (doodad == null) return;

        children.remove(doodad.getId());
        doodad.setOwnerId("");
        doodad.setTeamColor(TeamColor.DEFAULT);
    }

    @Override
    public DoodadOwnable getChild(String doodadId) {
        Doodad doodad = battleground.getDoodad(doodadId);
        return (doodad instanceof DoodadOwnable) ? (DoodadOwnable) doodad : null;
    }

    @Override
    public boolean isChild(String doodadId) {
        return children.contains(doodadId);
    }

    @Override
    public DoodadInstance createInstance(BattlegroundInstance battlegroundInstance) {
        return new DoodadGroupInstance(battlegroundInstance, this);
    }

    @Override
    public void destroy() {
        BgManager.unregisterListener(this);
    }

    @Override
    public void debugTick() {}

    @Override
    public CompoundTag readNBT(CompoundTag tag) {
        ListTag<StringTag> childrenList = tag.getListTag("children").asStringTagList();

        for (StringTag childTag : childrenList){
            children.add(childTag.getValue());
        }

        return super.readNBT(tag);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        ListTag<StringTag> childrenList = new ListTag<>(StringTag.class);
        for (String child : children){
            childrenList.add(new StringTag(child));
        }
        tag.put("children", childrenList);

        return super.writeNBT(tag);
    }

    @EventHandler
    public void onBattlegroundLoad(BattlegroundLoadEvent event){
        if (event.getBattleground() == battleground)
            updateChildren();
    }

    @EditCommand(cmd ="addChild")
    public void addChild(Player player, String[] args){
        if (args.length == 0) {

            return;
        } else if (args.length > 1){
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return;
        }

        args[0] = args[0].replaceAll("\"", "");

        Doodad doodad = battleground.getDoodad(args[0]);
        if (doodad == null){
            player.sendMessage(ChatColor.RED + "Doodad of id<" + args[0] + "> does not exist!");
            return;
        } else if (!(doodad instanceof DoodadOwnable)) {
            player.sendMessage(ChatColor.RED + "Doodad of id<" + args[0] + "> cannot be a child!");
            return;
        }

        addChild((DoodadOwnable) doodad);
        player.sendMessage(ChatColor.GOLD + "Doodad of id<" + args[0] + "> added to <" + id + ">");
    }

    @EditCommand(cmd ="removeChild")
    public void removeChild(Player player, String[] args){
        if (args.length == 0) {

            return;
        } else if (args.length > 1){
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return;
        }

        args[0] = args[0].replaceAll("\"", "");

        if (!isChild(args[0])){
            player.sendMessage(ChatColor.RED + "Doodad of id<" + args[0] + "> is not a child of <" + id + ">!");
            return;
        }

        DoodadOwnable doodad = getChild(args[0]);

        if (doodad == null)
            children.remove(args[0]);
        else
            removeChild(doodad);

        player.sendMessage(ChatColor.GOLD + "Doodad of id<" + args[0] + "> removed from <" + id + ">");
    }
}
