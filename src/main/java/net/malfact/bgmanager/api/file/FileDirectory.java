package net.malfact.bgmanager.api.file;

import net.malfact.bgmanager.BgManager;

import java.io.File;

public enum FileDirectory {

    WORLD_BASE(null, "Battlegrounds"){
        @Override
        public File getDirectory() {
            return null;
        }
    },
    WORLD_SAVE(WORLD_BASE, "Saves"),
    WORLD_INSTANCE(WORLD_BASE, "Instances"),
    WORLD_TEMP(WORLD_BASE, "Instances"),

    DATA_BASE(null, ""){
        @Override
        public File getDirectory() {
            return BgManager.getInstance().getDataFolder();
        }
    },
    DATA_BATTLEGROUNDS(DATA_BASE, "battlegrounds"),
    DATA_BATTLEGROUNDS_TEMP(DATA_BASE, "battlegrounds_temp");

    private final FileDirectory parent;
    private final String name;

    FileDirectory(FileDirectory parent, String name){
        this.parent = parent;
        this.name = name;
    }

    public File getDirectory(){
        File directory = new File(parent.getDirectory(), name);
        if (!directory.exists())
            directory.mkdirs();

        return directory;
    }

    public File[] getFiles(){
        File directory = getDirectory();
        if (directory == null)
            return new File[0];

        return directory.listFiles();
    }

    public boolean clearDirectory(){
        boolean missedDelete = false;

        for (File file : getFiles()){
            if (!FileUtil.delete(file))
                missedDelete = true;
        }
        return !missedDelete;
    }
}
