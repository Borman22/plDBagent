package tv.sonce.pldbagent.controller;
/**
 *  Этому классу позволяется апдейтить БД. Он берет распарсеный файл, проверяет, каждое событие и если в БД такого события нет, он добавляет это событие в БД
 */

import tv.sonce.pldbagent.model.DBAgent;
import tv.sonce.pldbagent.model.FileAgent;

import java.util.List;

public class DBWriter {
    public static void addEventsFromNewFile(List<FileParser.Event> parseredFile, DBAgent dbAgent, String fileAgent) {
        // Каждое событие из файла надо сравнить с БД и если этого события нет в БД, то добавить ее в БД
        // Запрашиваем у БД инфу про конкретное событие

        // Анализируем. Если 2 или больше одинаковых события - одно удаляем. Если событие есть - идем дальше. Если события нет - добавляем


    }

    // Этот метод добавляет файл (имя, дату создания, путь к директории) в БД, чтобы при следующем запуске его не парсить
    public static void addNewFile(FileAgent fileAgent, DBAgent dbAgent) {
    }
}
