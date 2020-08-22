package net.malfact.bgmanager.response;

import java.util.UUID;

public interface ResponseListener {

    void receiveResponse(UUID uuid, boolean confirm);
}
