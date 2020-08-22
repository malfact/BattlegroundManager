package net.malfact.bgmanager.api.command;

public interface SubCommandContainer {

    SubCommandContainer registerSubCommand(String cmd, SubCommand executor);
}
