package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;

import java.io.File;

/**
 * Этот класс занимается поиском новых файлов
 */

public class NewFilesFinder {
    private File[] files;
    private File[] newFiles = null;
    private DBAgent dbAgent;
    private String path;


    public NewFilesFinder(FileAgent fileAgent, DBAgent dbAgent){
        this.path = fileAgent.getPath();
        this.files = fileAgent.getFolderEntries();
        this.dbAgent = dbAgent;
    }

    public File[] getNewFiles(){
        return newFiles;
    }
}
