package com.example.controller;

import com.example.socket.SocketServer;
import com.example.util.LoggerUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.logging.*;

@Path("/ppobntl")
public class Ntl_01ASignController_28_me1 {

    // Mendapatkan logger dari LoggerUtil
    private static final Logger logger = LoggerUtil.getLogger(Ntl_01ASignController_28_me1.class);

    //-----------------------------------------------------------------
    //1. ppob signin
    //-----------------------------------------------------------------
    @POST
    @Path("/post_ppob_login_me")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addBanklogin(String jsonBody) {
        
        String vstatus = "ERROR";
        String vstatus_msg = "";
        String message = "";
        String serverResponse = "";
        JsonArray serverResponseArrayParsing = new JsonArray();

        // Validasi input JSON kosong atau null
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            vstatus_msg = "JSON input tidak boleh kosong.";
            return createErrorResponse(vstatus, vstatus_msg);
        }

        JsonArray inputArray;
        try {
            inputArray = JsonParser.parseString(jsonBody).getAsJsonArray();
        } catch (Exception e) {
            vstatus_msg = "Format JSON tidak valid.";
            return createErrorResponse(vstatus, vstatus_msg);
        }

        SocketServer socketServer = new SocketServer();  // Buat objek SocketServer

        for (JsonElement element : inputArray) {
            JsonObject inputObject = element.getAsJsonObject();

            if (!inputObject.has("type_msg")) {
                vstatus_msg = "JSON input Type Message kosong.";
                return createErrorResponse(vstatus, vstatus_msg);
            }

            // Konversi JSON ke message
            message = GetJsonToMsg_2800(jsonBody);
            if (message.isEmpty()) {
                vstatus_msg = "JSON input pembentuk message ada yang kosong.";
                return createErrorResponse(vstatus, vstatus_msg);
            }

            // Kirim pesan melalui socket
            serverResponse = socketServer.sendMessage(message);
            if (serverResponse.isEmpty()) {
                vstatus_msg = "Respon server kosong/Invalid message.";
                return createErrorResponse(vstatus, vstatus_msg);
            }

            vstatus     = "OK";
            vstatus_msg = "Respon diterima.";

            // Parsing respons dari server
            try {
                serverResponseArrayParsing = JsonParser.parseString(ResponServerParsing_2810(serverResponse)).getAsJsonArray();
            } catch (Exception e) {
                vstatus = "ERROR";
                vstatus_msg = "Gagal memproses respon server.";
            }

            logger.info("Valid JSON diterima: " + inputObject);
        }

        return createSuccessResponse(vstatus, vstatus_msg, message, serverResponse, serverResponseArrayParsing);
    }

    /**
     * Fungsi untuk membuat JSON response error
     */
    private String createErrorResponse(String status, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("Status", status);
        response.addProperty("Info", message);
        return new GsonBuilder().setPrettyPrinting().create().toJson(response);
    }

    /**
     * Fungsi untuk membuat JSON response sukses
     */
    private String createSuccessResponse(String status, String message, String clientMessage, String serverResponse, JsonArray serverResponseParsing) {
        JsonObject response = new JsonObject();
        response.addProperty("Status", status);
        response.addProperty("Info", message);
        response.addProperty("ClientInquery", clientMessage);
        response.add("ClientInqueryParsing", JsonParser.parseString(ReguestClientParsing_2800(clientMessage)));
        response.addProperty("ServerResponse", serverResponse);
        response.add("serverResponseParsing", serverResponseParsing);

        String formattedJson = new GsonBuilder().setPrettyPrinting().create().toJson(response);
        logger.info("INFO: Response JSON: " + formattedJson);
        return formattedJson;
    }

    public String GetJsonToMsg_2800(String jsonBody) {
        JsonArray jsonArrayResponse = new JsonArray();
        JsonObject jsonObjectResponse = new JsonObject();
        JsonObject responseObject = new JsonObject();
    
        String message = "";
        logger.info(" - Send Request Body : " + jsonBody);
    
        JsonArray inputArray = JsonParser.parseString(jsonBody).getAsJsonArray();
    
        if (inputArray.size() == 0) {
            responseObject.addProperty("status", "ERROR");
            responseObject.addProperty("message", "JSON input tidak boleh kosong.");
            return responseObject.toString();
        }
    
        for (JsonElement element : inputArray) {
            JsonObject inputObject = element.getAsJsonObject();
            int typeMessage = inputObject.get("type_msg").getAsInt();
    
            if (typeMessage == 1) {
                // Validasi `message`
                if (!inputObject.has("message") || inputObject.get("message").getAsString().isEmpty()) {
                    message = "";
                } else {
                    message = inputObject.get("message").getAsString();
    
                    // Gunakan ReguestClientParsing_2800 untuk mendapatkan hasil format JSON
                    String formattedJsonOutput = ReguestClientParsing_2800(message);
    
                    // Cetak JSON terformat di log
                    logger.info(" - Send Request Body Parsing Type 1: " + formattedJsonOutput);
                }
            } else if (typeMessage == 2) {
                // Mengambil setiap field dengan nilai default jika field tidak ada
                String mti = inputObject.has("mti") ? inputObject.get("mti").getAsString() : "";
                String bitMap = inputObject.has("bit_map") ? inputObject.get("bit_map").getAsString() : "";
                String dtlTrans = inputObject.has("dtl_trans") ? inputObject.get("dtl_trans").getAsString() : "";
                String actionCode = inputObject.has("action_code") ? inputObject.get("action_code").getAsString() : "";
                String mtiLen = inputObject.has("mti_len") ? inputObject.get("mti_len").getAsString() : "";
                String switcherId = inputObject.has("switcher_id") ? inputObject.get("switcher_id").getAsString() : "";
    
                message = mti + bitMap + dtlTrans + actionCode + mtiLen + switcherId;
    
                // Menyusun JSON sesuai format
                jsonObjectResponse.addProperty("mti", mti);
                jsonObjectResponse.addProperty("bitMap", bitMap);
                jsonObjectResponse.addProperty("dtlTrans", dtlTrans);
                jsonObjectResponse.addProperty("actionCode", actionCode);
                jsonObjectResponse.addProperty("mtiLen", mtiLen);
                jsonObjectResponse.addProperty("switcherId", switcherId);
                jsonArrayResponse.add(jsonObjectResponse);
    
                // Konversi ke format JSON yang rapi
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String formattedJsonOutput = gson.toJson(jsonArrayResponse);
                logger.info(" - Send Request Body Parsing Type 2: " + formattedJsonOutput);
            }
        }
    
        return message;
    }  
    
    //-----------------------------------------------------------------
    // Parsing Request Client 2800
    //-----------------------------------------------------------------
    public static String ReguestClientParsing_2800(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
    
        try {
            // Daftar nama field
            String[] fields = {"mti", "bit_map", "dtl_trans", "action_code", "apd_len", "switcher_id"};
    
            // Panjang masing-masing field
            int[] fieldLengths = {4, 16, 14, 3, 3, 7};
            int currentIndex = 0;
    
            // Parsing data awal
            for (int i = 0; i < fields.length; i++) {
                if (inputMessage.length() >= currentIndex + fieldLengths[i]) {
                    String value = inputMessage.substring(currentIndex, currentIndex + fieldLengths[i]);
                    parsedObject.addProperty(fields[i], value.trim());
                    currentIndex += fieldLengths[i];
                } else {
                    parsedObject.addProperty(fields[i], "");
                    break;
                }
            }
    
            // Tambahkan objek yang telah diparsing ke JsonArray
            jsonArray.add(parsedObject);
    
        } catch (Exception e) {
            // Tangani error dengan menambahkan objek error ke JsonArray
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("status", "ERROR");
            errorObject.addProperty("message", "Terjadi kesalahan saat memproses inputMessage: " + e.getMessage());
            jsonArray.add(errorObject);
        }
    
        // Kembalikan JSON dalam bentuk String
        return new GsonBuilder().setPrettyPrinting().create().toJson(jsonArray);
    }
    
    //-----------------------------------------------------------------
    // Parsing respon server netman 2810
    //-----------------------------------------------------------------
    public static String ResponServerParsing_2810(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
    
        try {
            // Daftar nama field
            String[] fields = {
                "mti", "bit_map", "dtl_trans", "rc", "action_code", "apd_len", "switcher_id"
            };
    
            // Panjang masing-masing field
            int[] fieldLengths = {4, 16, 14, 4, 3, 3, 7 };
            int currentIndex = 0;
    
            // Parsing data awal
            for (int i = 0; i < fields.length; i++) {
                if (inputMessage.length() >= currentIndex + fieldLengths[i]) {
                    String value = inputMessage.substring(currentIndex, currentIndex + fieldLengths[i]);
                    parsedObject.addProperty(fields[i], value.trim());
                    currentIndex += fieldLengths[i];
                } else {
                    parsedObject.addProperty(fields[i], "");
                    break;
                }
            }

            // Tambahkan objek yang telah diparsing ke JsonArray
            jsonArray.add(parsedObject);
    
        } catch (Exception e) {
            // Tangani error dengan menambahkan objek error ke JsonArray
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("status", "ERROR");
            errorObject.addProperty("message", "Terjadi kesalahan saat memproses inputMessage: " + e.getMessage());
            jsonArray.add(errorObject);
        }
    
        // Kembalikan hasil JSON
        return new Gson().toJson(jsonArray);
    }
}
