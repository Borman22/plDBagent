package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;
import tv.sonce.pldbagent.model.tables.Row_file_names;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Этот класс занимается поиском удаленных и новых файлов
 */

public class FileFinder {

    private File[] existingFilesInDir;
    private DBAgent dbAgent;
    private String pathToDir;
    private String pathToDirForQuery;

    public FileFinder(FileAgent fileAgent, DBAgent dbAgent) {
        this.pathToDir = fileAgent.getPathToDir();
        this.pathToDirForQuery = "\'" + pathToDir.replace("\\", "\\\\") + "\'";
        this.existingFilesInDir = fileAgent.getFolderEntries();
        this.dbAgent = dbAgent;
    }

    public List<Row_file_names> getDeletedFilesInDir(){
        // Получаем список существующих файлов в БД и смотрим, есть ли файлы,
        // которые по имени и дате создания не соответствуют ни одному файлу из текущей дериктории
        if(existingFilesInDir == null)
            return null;   // Если нет доступа к директории - не значит, что файлы из нее удалены

        List<Row_file_names> existingFilesInDB = getExistingFilesInDirInDB(dbAgent);
        List<Row_file_names> deletedFiles = new ArrayList<>();

        boolean find;

        for (Row_file_names currentFileInDB : existingFilesInDB) {
            find = false;
            for (File currentFileInDir : existingFilesInDir) {
                if((currentFileInDB.getDate_create() == currentFileInDir.lastModified()) && (currentFileInDB.getFile_name().equals(currentFileInDir.getName()))){
                    find = true;
                    break;
                }
            }
            if(!find){
                deletedFiles.add(currentFileInDB);
            }
        }
        return deletedFiles;
    }

    public List<Row_file_names> getNewFilesInDir(){
        List<Row_file_names> newFiles = getExistingFilesInDirInDB(dbAgent);
        return newFiles;
    }

    // получим из БД список всех когда либо существовавших файлов в заданой папке
    private List<Row_file_names> getAllFilesInDirInDB(DBAgent dbAgent){
        String query = "SELECT * FROM file_names, path WHERE file_names.id_path = path.id AND path.path = " + pathToDirForQuery;
        return getTable_file_names(dbAgent, query);
    }

    // получим из БД список всех удаленных файлов из заданой папки
    private List<Row_file_names> getDeletedFilesInDirInDB(DBAgent dbAgent){
        String query = "SELECT * FROM file_names, path WHERE file_names.date_delete != 0 AND file_names.id_path = path.id AND path.path = " + pathToDirForQuery;
        return getTable_file_names(dbAgent, query);
    }

    // получим из БД список всех существующих файлов в заданой папке
    private List<Row_file_names> getExistingFilesInDirInDB(DBAgent dbAgent){
        String query = "SELECT * FROM file_names, path WHERE file_names.date_delete = 0 AND file_names.id_path = path.id AND path.path = " + pathToDirForQuery;
        return getTable_file_names(dbAgent, query);
    }

    private List<Row_file_names> getTable_file_names(DBAgent dbAgent, String query) {
        ResultSet rs = null;
        try {
            rs = dbAgent.executeQuery(query);
        } catch (SQLException e) {
            System.out.println("Не удалось получить данные из БД");
            System.out.println(e.getLocalizedMessage());
        }
        if(rs == null) return null;

        List<Row_file_names> table_file_names = new ArrayList<>();
        try {
            int tempIdPk;
            String tempFileName;
            long tempDateCreate;
            long tempDateDeleted;
            int tempPathIdFk;

            while (rs.next()) {
                tempIdPk = rs.getInt("id");
                tempFileName = rs.getString("file_name");
                tempDateCreate = rs.getLong("date_create");
                tempDateDeleted = rs.getLong("date_delete");
                tempPathIdFk = rs.getInt("id_path");

                table_file_names.add(new Row_file_names(tempIdPk, tempFileName, tempDateCreate, tempDateDeleted, tempPathIdFk));
            }
            return table_file_names;
        } catch (SQLException e) {
            System.out.println("Не получилось прочитать данные из ResultSet");
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

}
