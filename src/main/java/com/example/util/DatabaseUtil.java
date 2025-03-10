package com.example.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseUtil {

    private static final String PROPERTIES_FILE = "db.properties";

    public static Connection getConnection() {
        Connection connection = null;
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new RuntimeException("File " + PROPERTIES_FILE + " tidak ditemukan di classpath!");
            }

            // Load properties file
            Properties props = new Properties();
            props.load(input);

            // Baca konfigurasi database
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            // Load Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Buat koneksi baru ke database
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Koneksi ke database berhasil!");

        } catch (Exception e) {
            System.err.println("Gagal membuat koneksi ke database!");
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage(), e);
        }
        return connection;
    }

    // Metode main untuk testing koneksi
    public static void main(String[] args) {
        try (Connection testConn = DatabaseUtil.getConnection()) {
            if (testConn != null) {
                System.out.println("Tes koneksi berhasil!");
            } else {
                System.out.println("Tes koneksi gagal!");
            }
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan saat menguji koneksi:");
            e.printStackTrace();
        }
    }
}
