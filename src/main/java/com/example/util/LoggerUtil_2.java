package com.example.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil_2 {
    private static final String LOG_DIRECTORY = "src/main/resources/logs";

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false); // Hindari duplikasi log di konsol

        try {
            // Buat folder log jika belum ada
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Nama file log dengan pola tanggal
            String logFileName = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_LogClientApp.log";
            FileHandler fileHandler = new FileHandler(LOG_DIRECTORY + "/" + logFileName, true);

            // Formatter log dengan timestamp milidetik
            Formatter customFormatter = new Formatter() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                @Override
                public String format(LogRecord record) {
                    return dateFormat.format(new Date(record.getMillis())) + " - " +
                           record.getLevel() + ": " +
                           formatMessage(record) + System.lineSeparator();
                }
            };

            fileHandler.setFormatter(customFormatter);
            logger.addHandler(fileHandler);

            // Tambahkan ConsoleHandler agar log juga tampil di terminal
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO); // Bisa diubah sesuai kebutuhan (INFO, WARNING, SEVERE, dll)
            consoleHandler.setFormatter(customFormatter);
            logger.addHandler(consoleHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }
}
