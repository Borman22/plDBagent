package tv.sonce.pldbagent.model;
/**
 * Этот класс инкапсулирует в себе БД и операции для работы с ней
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DBAgent {
    private Connection connection = null;
    private String settings = "?serverTimezone=UTC&useSSL=false";

    public DBAgent(String hostPort, String dbName, String login, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new SQLException("Соединение с базой данных уже установлено." +
                    "Чтобы установить новое соединение, сначала необходимо закрыть текущее");
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("ОШИБКА! Не подключен SQL драйвер JDBC!", e);
        }

        connection = DriverManager.getConnection(hostPort + dbName + settings,login, password);
        if ((connection == null) || (connection.isClosed()))
            throw new SQLException("Не удалось подключитсья к базе данных");
    }

    public void markFilesAsDeleted(Map<Long, String> files){

    }

    public void addNewFilesInDB(File[] files){

    }

    private boolean isConnected() {
        try {
            if (connection != null && !connection.isClosed())
                return true;
        } catch (SQLException e) {
//            DoNothing
        }
        return false;
    }


    public void close(){
        if (isConnected())
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Не удалось закрыть соединение с БД");
                System.out.println(e.getLocalizedMessage());
            }
    }


    protected void finalize() {
        if(this.isConnected())
            close();
    }
}
