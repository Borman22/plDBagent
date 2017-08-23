package tv.sonce.pldbagent;
/**
 * Этот класс запускает программу и инициализирует все классы.
 */

import tv.sonce.pldbagent.controller.MainController;
import tv.sonce.pldbagent.model.DBConnector;
import tv.sonce.pldbagent.model.FileAgent;

import java.sql.SQLException;

public class Main {
    private static String pathToStorage = "\\\\storage\\Solarmedia\\pl_lists\\"; // пути к дерикториям, где лежат плейлисты
    private static String pathToD = "d:\\Borman\\pl_lists\\";
    private static String pathToInmedia = "\\\\inmedia\\AsRunLogs\\";

    private static String dbName = "playlistdb";
    private static String dbHost = "jdbc:mysql://localhost:3306/";
    private static String dbLogin = "root";
    private static String dbPassword = "root";
    public static long currentDate;

    public static void main(String[] args) {
        currentDate = System.currentTimeMillis();
        FileAgent fileAgentStorage = new FileAgent(pathToStorage);
        FileAgent fileAgentInmedia = new FileAgent(pathToInmedia);
        FileAgent fileAgentD = new FileAgent(pathToD);

        try {
            DBConnector dbConnector = new DBConnector(dbHost, dbName, dbLogin, dbPassword);
            MainController mainController = new MainController(dbConnector, fileAgentStorage, fileAgentInmedia, fileAgentD);
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            if (e.getCause() != null)
                System.out.println(e.getCause().getLocalizedMessage());
        }
    }
}
