package net.malfact.bgmanager.api.battleground;

public enum BattlegroundStatus {
    WAITING("Waiting for Players", true),
    STARTING("Battle about to begin", true),
    IN_PROGRESS("Battle in progress", true),
    ENDING("Battle Ending", false),
    FINISHED("Battle Over", false);

    public final String text;
    public final boolean allowEntry;

    private BattlegroundStatus(String text, boolean allowEntry){
        this.text = text;
        this.allowEntry = allowEntry;
    }
}
