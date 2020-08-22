package net.malfact.bgmanager.response;

import java.util.HashMap;
import java.util.UUID;

public final class ResponseManager {

    private static ResponseManager instance;

    public static ResponseManager get(){
        if (instance == null){
            instance = new ResponseManager();
        }

        return instance;
    }

    private HashMap<UUID, ResponseListener> listeners = new HashMap<>();

    private ResponseManager(){}

    public void registerResponseListener(UUID uuid, ResponseListener listener){
        listeners.put(uuid, listener);
    }

    public void unregisterResponseListener(UUID uuid){
        listeners.remove(uuid);
    }

    public void sendResponse(UUID uuid, boolean confirm){
        if (listeners.get(uuid) == null)
            return;

        listeners.get(uuid).receiveResponse(uuid, confirm);
    }

}
