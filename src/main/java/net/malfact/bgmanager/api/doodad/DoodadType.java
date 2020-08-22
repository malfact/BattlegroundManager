package net.malfact.bgmanager.api.doodad;

import net.malfact.bgmanager.doodad.*;

import java.util.HashMap;
import java.util.Map;

public class DoodadType {

    private static final Map<String, DoodadType> byName = new HashMap<>();
    private static final Map<Class<? extends Doodad>, DoodadType> byClass = new HashMap<>();

    public static final DoodadType FLAG_SPAWN = registerDoodadType("FLAG_SPAWN", DoodadFlagSpawn.class);
    public static final DoodadType FLAG_CAPTURE = registerDoodadType("FLAG_CAPTURE", DoodadFlagCapture.class);
    public static final DoodadType FLAG_STAND = registerDoodadType("FLAG_STAND", DoodadFlagStand.class);
    public static final DoodadType RADIAL_FIELD = registerDoodadType("RADIAL_FIELD", DoodadRadialField.class);
    public static final DoodadType GATE = registerDoodadType("GATE", DoodadGate.class);
    public static final DoodadType GRAVEYARD = registerDoodadType("GRAVEYARD", DoodadGraveyard.class);
    public static final DoodadType BUFF = registerDoodadType("BUFF", DoodadBuff.class);

    /**
     * Register a new DoodadType with the given name and class
     * @param name Name to register
     * @param clazz Class to register
     * @return The registered DoodadType
     */
    public static DoodadType registerDoodadType(String name, Class<? extends Doodad> clazz){
        if (name == null || clazz == null)
            throw new IllegalArgumentException("Name or Class cannot be NULL");

        if (byName.containsKey(name) || byClass.containsKey(clazz))
            throw new IllegalArgumentException("Cannot set already-set type");

        DoodadType newType = new DoodadType(name, clazz);
        byName.put(name, newType);
        byClass.put(clazz, newType);
        return newType;
    }

    /**
     * Get a DoodadType by name
     * @param name Name of DoodadType to fetch
     * @return Resulting DoodadType, or null if not found
     */
    public static DoodadType getByName(String name){
        System.out.println(name + ": "  + byName.get(name.toUpperCase()).toString());
        return byName.get(name.toUpperCase());
    }

    /**
     * Get a DoodadType by class
     * @param clazz Class of DoodadType to fetch
     * @return Resulting DoodadType, or null if not found
     */
    public static DoodadType getByClass(Class<? extends Doodad> clazz){
        return byClass.get(clazz);
    }

    /**
     * Returns an array of all the registered {@link DoodadType}s.
     *
     * @return Array of DoodadTypes.
     */
    public static DoodadType[] values(){
        return byName.values().toArray(new DoodadType[0]);
    }

    private final String name;
    private final Class<? extends Doodad> clazz;

    private DoodadType(String name, Class<? extends Doodad> clazz){
        this.name = name.toUpperCase();
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Doodad> getDoodadClass() {
        return this.clazz;
    }
}
