package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("setting.properties")) {
            if (input == null) {
                throw new IOException("Tidak ditemukan file Setting socket.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHost() {
        return properties.getProperty("socket.host");
    }

    public static int getPort() {
        return Integer.parseInt(properties.getProperty("socket.port"));
    }
}
