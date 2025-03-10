package com.example.socket; // Ganti dengan nama paket yang sesuai

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.example.util.ConfigReader;

public class SocketServer {
    // private final String host = "10.14.152.104";  // Ganti dengan IP yang sesuai
    // private final int port = 55101;  // Ganti dengan port yang sesuai
    private final String host = ConfigReader.getHost();
    private final int    port = ConfigReader.getPort();

    //-----------------------------------------------------------------
    // Fungsi untuk menghubungi server dan mengirim pesan
    //-----------------------------------------------------------------
    public String sendMessage(String message) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // logger.info(" - Mengirim pesan: " + message);
            // Tambahkan karakter EOF
            message += (char) 255; 
            out.print(message);
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            String out_response = response.toString().replace((char) 255, ' ').trim();
            // logger.info(" - Terima pesan: " + out_response);
            return out_response;

        } catch (Exception e) {
            // logger.info(" - Error: " + e.getMessage());
            return "Gagal terhubung ke server.";
        }
    }
    
}
