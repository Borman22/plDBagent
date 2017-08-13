package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;

import java.io.File;
import java.util.Map;

/**
 * Этот класс занимается поиском удаленных файлов
 */

public class DeletedFileFinder {

    private File[] files;
    private Map<Long, String> deletedFiles;
    private DBAgent dbAgent;
    private String path;

    public DeletedFileFinder(FileAgent fileAgent, DBAgent dbAgent) {
        this.path = fileAgent.getPath();
        this.files = fileAgent.getFolderEntries();
        this.dbAgent = dbAgent;
    }

    public Map<Long, String> getDeletedFiles(){
        return deletedFiles;
    }

}
