package net.malfact.bgmanager.doodad;

import net.malfact.bgmanager.api.battleground.BattlegroundInstance;

public class DoodadFlagStandInstance extends DoodadBaseInstance{

    protected final DoodadFlagCaptureInstance flagCaptureInstance;
    protected final DoodadFlagSpawnInstance flagSpawnInstance;

    protected DoodadFlagStandInstance(BattlegroundInstance battlegroundInstance, DoodadFlagStand doodad) {
        super(battlegroundInstance, doodad);

        flagCaptureInstance = new DoodadFlagCaptureInstance(battlegroundInstance, doodad.flagCaptureDoodad);
        flagSpawnInstance = new DoodadFlagSpawnInstance(battlegroundInstance, doodad.flagSpawnDoodad);
    }

    @Override
    public void destroy() {
        super.destroy();
        flagCaptureInstance.destroy();
        flagSpawnInstance.destroy();
    }

    @Override
    public void tick() {
        flagSpawnInstance.tick();
        flagCaptureInstance.tick();

        flagCaptureInstance.setActive(flagSpawnInstance.flagSpawned);
    }
}
