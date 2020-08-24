package net.malfact.bgmanager.api.world;

import net.malfact.bgmanager.api.FileUtil;

import java.io.File;

public enum WorldDirectory {

    BASE(null, "Battlegrounds"),
    SAVE(BASE.directory, "Saves"),
    INSTANCE(BASE.directory, "Instances"),
    TEMP(BASE.directory, "Instances");

    private final File directory;

    WorldDirectory(File parent, String name){
        directory = new File(parent, name);
    }

    public File getDirectory(){
        if (!directory.exists())
            directory.mkdirs();

        return directory;
    }

    public boolean clearDirectory(){
        boolean missedDelete = false;

        for (File file : directory.listFiles()){
            if (!FileUtil.delete(file))
                missedDelete = true;
        }
        return !missedDelete;
    }
}
