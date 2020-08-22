package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;

public interface DoodadInstance {

    BattlegroundInstance getBattlegroundInstance();

    void destroy();

    void tick();
}
