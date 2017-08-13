package tv.sonce.pldbagent.controller;

import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;

/**
 * Этот класс управляет операциями поиска файлов и модификации БД
 */

public class MainController {

    public MainController(DBAgent dbAgent, FileAgent... fileAgents) {

        for (int i = 0; i < fileAgents.length; i++) {
            // получаем список удаленных файлов
            // модифицируем БД

            // получаем список новых файлов
            // модифицируем ДБ
        }
    }
}
