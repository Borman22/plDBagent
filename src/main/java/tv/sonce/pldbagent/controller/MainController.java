package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.Main;
import tv.sonce.pldbagent.model.DBConnector;
import tv.sonce.pldbagent.model.FileAgent;
import tv.sonce.pldbagent.model.tables.Row_file_names;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Этот класс управляет операциями поиска файлов и модификации БД
 */

public class MainController {
    private FileAgent[] fileAgents;
    private DBConnector dbConnector;

    public MainController(DBConnector dbConnector, FileAgent... fileAgents) {

        this.dbConnector = dbConnector;
        this.fileAgents = fileAgents;

        // Прямо из конструктора вызываем все необходимые действия
        // Сначала надо проверить, есть ли в БД путь, по которому хотят получить статистику, если нет - добавим его и будем следить за этой папкой всегда
        processPathToDir();

        // Теперь находим все удаленные файлы и помечаем в БД, что файлы удалены.
        processAllDeletedFiles();
        System.out.println();

        // Находим все вновь созданные файлы и помещаем инфу про них в БД (Дату последней модификации (создания) и имя файла)
        processAllNewFiles();
    }

    private void processAllNewFiles() {
        FileParser fileParser = new FileParser();


        // получаем список новых файлов в каждой папке по очереди - модифицируем БД, анализируем файл, добавляем события в БД. Переходим к след. файлу
        for (FileAgent fileAgent : fileAgents) {  // проходимся по всем папкам, где лежат сохраненные плейлисты
            FileFinder tempFileFinder = new FileFinder(fileAgent, dbConnector);
            List<File> newFiles = tempFileFinder.getNewFilesInDir();

            // получим ID, под которым хранится в БД путь к данной папке
            int id_path = -1;
            try {
                ResultSet rs = dbConnector.executeQuery("SELECT id FROM path WHERE path = " + fileAgent.getPathToDirForQuery());
                if (rs.next()) {
                    id_path = rs.getInt("id");
                }

            } catch (SQLException e) {
                System.out.println("Не удалось получить из БД id, под которым сохранен путь к папке, где лежит данный файл " + fileAgent.getPathToDir());
                System.out.println(e.getLocalizedMessage());
            }

            // надо добавить файл в БД и получить его id, чтобы потом использовать в запросах
            int id_file_names;

            if (newFiles != null) {
                System.out.println("Количество новых файлов в папке " + fileAgent.getPathToDir() + " равно:" + newFiles.size());

                // Надо распарсить каждый новый файл и добавить из него каждое событие в БД
                for (File newFile : newFiles) { // проходимся по каждому файлу (плейлисту)
                    id_file_names = DBAgentUtil.addNewFile(newFile, fileAgent, dbConnector, id_path);
                    System.out.println("id_file_names = " + id_file_names);
                    if (id_file_names < 0) {
                        System.out.println("Не удалось найти в БД такой файл и не удалось его туда добавить: " + newFile.getName());
                    }

                    List<FileParser.Event> parseredFile = fileParser.parse(newFile);
                    if (parseredFile == null) // Не удалось распарсить или формат не .xml
                        continue;

                    // Теперь надо каждое событие из каждого файла сравнить с БД и если есть новая инфа - добавить ее в БД
                    DBAgentUtil.addAllEventsFromNewFile(parseredFile, dbConnector, id_file_names);


                }

            } else {
                System.out.println("Не могу получить доступ к папке " + fileAgent.getPathToDir());
            }
        }
    }

    private void processPathToDir() {
        Set<String> currentPathList = new HashSet<>();
        Set<String> oldPathList = new HashSet<>();
        for (FileAgent fa : fileAgents) { // берем путь с каждого файл агента и с БД и если находим что-то новое - добавляем в БД
            currentPathList.add(fa.getPathToDir());
        }

        try {
            ResultSet rs = dbConnector.executeQuery("SELECT path FROM path");
            while (rs.next()) {
                oldPathList.add(rs.getString("path"));
            }
        } catch (SQLException e) {
            System.out.println("Не удалось получить список путей ко всем папкам, за которыми следить программа");
            System.out.println(e.getLocalizedMessage());
            return;
        }

        currentPathList.removeAll(oldPathList); // Отнимем от нового списка старый и получим разницу. Ее добавим в БД
        if (currentPathList.size() != 0) {
            for (String tempPath : currentPathList) {
                try {
                    dbConnector.executeUpdate("INSERT INTO path (path) VALUES (\'" + tempPath.replace("\\", "\\\\") + "\')");
                } catch (SQLException e) {
                    System.out.println("Не удалось добавить новый путь в БД");
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }


    }

    private void processAllDeletedFiles() {
        // получаем список удаленных файлов - модифицируем БД, получаем список удаленных файлов в другой папке - модифицируем ДБ...
        for (FileAgent fileAgent : fileAgents) {
            FileFinder tempFileFinder = new FileFinder(fileAgent, dbConnector);
            List<Row_file_names> deletedFiles = tempFileFinder.getDeletedFilesInDir();
            if (deletedFiles != null) {
                System.out.println("Количество файлов, которые удалены из папки " + fileAgent.getPathToDir() + " равно:" + deletedFiles.size());
                for (Row_file_names deletedFile : deletedFiles) {
                    String query = "UPDATE file_names SET date_delete = " + Main.currentDate + " WHERE id = " + deletedFile.getId_pk();
                    try {
                        dbConnector.executeUpdate(query);
                    } catch (SQLException e) {
                        System.out.println("Не удалось в БД пометить файлы, как удаленные. Query = " + query);
                        System.out.println(e.getLocalizedMessage());
                    }
                    System.out.println(deletedFile);
                }
            } else {
                System.out.println("Не могу получить доступ к папке " + fileAgent.getPathToDir());
            }
        }
    }
}
