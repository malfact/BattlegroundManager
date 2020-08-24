package net.malfact.bgmanager.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtil {

    public static boolean delete(File source){
        File[] contents = source.listFiles();
        boolean missedDelete = false;
        if (contents != null){
            for (File file : contents){
                if (!delete(file))
                    missedDelete = true;
            }
        }
        return !missedDelete && source.canWrite() && source.delete();
    }

    public static void copy(File source, File target){
        try{
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName())){
                if (source.isDirectory()){
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    String[] files = source.list();
                    for (String file : files){
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copy(srcFile, destFile);
                    }
                } else {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

