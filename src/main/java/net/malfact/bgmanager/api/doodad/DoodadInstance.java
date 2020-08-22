package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;

public interface DoodadInstance {

    BattlegroundInstance getInstance();

    void destroy();

    void tick();
}
