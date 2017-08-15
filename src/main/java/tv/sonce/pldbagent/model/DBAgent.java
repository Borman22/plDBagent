package tv.sonce.pldbagent.model;
/**
 * Этот класс инкапсулирует в себе БД и операции для получения к ней доступа
 */

import java.io.File;
import java.sql.*;
import java.util.Map;

public class DBAgent {
    private Connection connection = null;
    private Statement statement = null;
    private String settings = "?serverTimezone=UTC&useSSL=false";

    public DBAgent(String hostPort, String dbName, String login, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new SQLException("Соединение с базой данных уже установлено." +
                    "Чтобы установить новое соединение, сначала необходимо закрыть текущее");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
//            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("ОШИБКА! Не подключен SQL драйвер JDBC!", e);
        }

        connection = DriverManager.getConnection(hostPort + dbName + settings,login, password);
        if ((connection == null) || (connection.isClosed()))
            throw new SQLException("Не удалось подключитсья к базе данных");
        statement = connection.createStatement();
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

    public ResultSet executeQuery(String query) throws SQLException { // пока сделаем так. потом переделаем по человечески
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    public int executeUpdate(String query) throws SQLException { // пока сделаем так. потом переделаем по человечески
        return statement.executeUpdate(query);
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
