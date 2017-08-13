package tv.sonce.pldbagent.model;
/**
 * Этот класс инкапсулирует каталог с файлами и все функции, которые можно с ними производить.
 * Получает список всех файлов, которые лежат в заданной папке
 */

import java.io.File;

public class FileAgent {
    private File[] folderEntries = null;
    private String path;

    public FileAgent(String path){
        this.path = path;
        folderEntries = new File(path).listFiles();
    }

    public File [] getFolderEntries(){
        return folderEntries;
    }

    public String getPath(){
        return path;
    }

}
