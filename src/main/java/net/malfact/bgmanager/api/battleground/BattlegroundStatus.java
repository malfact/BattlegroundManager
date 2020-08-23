package net.malfact.bgmanager.api.battleground;

public enum BattlegroundStatus {
    INIT("", false),
    STARTING("Battle Starting", true),
    IN_PROGRESS("Time Remaining", true),
    ENDING("Battleground Closing", false),
    CLOSED("Battleground Closed", false);

    public final String text;
    public final boolean allowEntry;

    private BattlegroundStatus(String text, boolean allowEntry){
        this.text = text;
        this.allowEntry = allowEntry;
    }
}
