package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.Main;
import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;
import tv.sonce.pldbagent.model.tables.Row_file_names;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Этот класс управляет операциями поиска файлов и модификации БД
 */

public class MainController {
    FileAgent [] fileAgents;
    DBAgent dbAgent;

    public MainController(DBAgent dbAgent, FileAgent... fileAgents) {

        this.dbAgent = dbAgent;
        this.fileAgents = fileAgents;

        // Прямо из конструктора вызываем все необходимые действия
        // Сначала надо проверить, есть ли в БД путь, по которому хотят получить статистику, если нет - добавим его и будем следить за этой папкой всегда
        processPathToDir();
        processAllDeletedFiles();
    }

    private void processPathToDir() {
        Set<String> currentPathList = new HashSet<>();
        Set<String> oldPathList = new HashSet<>();
        for (FileAgent fa : fileAgents) { // берем путь с каждого файл агента и с БД и если находим что-то новое - добавляем в БД
            currentPathList.add(fa.getPathToDir());
        }

        try {
            ResultSet rs = dbAgent.executeQuery("SELECT path FROM path");
            while (rs.next()){
                oldPathList.add(rs.getString("path"));
            }
        } catch (SQLException e) {
            System.out.println("Не удалось получить список путей ко всем папкам, за которыми следить программа");
            System.out.println(e.getLocalizedMessage());
            return;
        }

        currentPathList.removeAll(oldPathList); // Отнимем от нового списка старый и получим разницу. Ее добавим в БД
        if(currentPathList.size() != 0){
            for(String tempPath : currentPathList){
                try {
                    dbAgent.executeUpdate("INSERT INTO path (path) VALUES (\'" + tempPath.replace("\\", "\\\\")+ "\')");
                } catch (SQLException e) {
                    System.out.println("Не удалось добавить новый путь в БД");
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }




    }

    private void processAllDeletedFiles() {
        // получаем список удаленных файлов - модифицируем БД, получаем список удаленных файлов в другой папке - модифицируем ДБ...
        for (int i = 0; i < fileAgents.length; i++) {
            FileFinder tempFileFinder = new FileFinder(fileAgents[i], dbAgent);
            List<Row_file_names> deletedFiles = tempFileFinder.getDeletedFilesInDir();
            if (deletedFiles != null) {
                System.out.println("Количество файлов, которые удалены из папки " + fileAgents[i].getPathToDir() + " равно:" + deletedFiles.size());
                for (int j = 0; j < deletedFiles.size(); j++) {
                    String query = "UPDATE file_names SET date_delete = " + Main.currentDate + " WHERE id = " + deletedFiles.get(j).getId_pk();
                    try {
                        dbAgent.executeUpdate(query);
                    } catch (SQLException e) {
                        System.out.println("Не удалось в БД пометить файлы, как удаленные. Query = " + query);
                        System.out.println(e.getLocalizedMessage());
                    }
                    System.out.println(deletedFiles.get(j));
                }
            } else{
                System.out.println("Не могу получить доступ к папке " + fileAgents[i].getPathToDir());
            }
        }
    }
}
