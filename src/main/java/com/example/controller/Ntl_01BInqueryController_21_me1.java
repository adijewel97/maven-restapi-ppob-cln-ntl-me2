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
public class Ntl_01BInqueryController_21_me1 {

    // Mendapatkan logger dari LoggerUtil
    private static final Logger logger = LoggerUtil.getLogger(Ntl_01BInqueryController_21_me1.class);

    //-----------------------------------------------------------------
    //1. ppob signin
    //-----------------------------------------------------------------
    @POST
    @Path("/post_ppob_inquery_me")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addBanklogin(String jsonBody) {
        
        String vstatus        = "ERROR";
        String vstatus_msg    = "";
        String message        = "";
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
            message = GetJsonToMsg_2100(jsonBody);
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
                serverResponseArrayParsing = JsonParser.parseString(ResponServerParsing_2110(serverResponse)).getAsJsonArray();
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
        response.add("ClientInqueryParsing", JsonParser.parseString(ReguestClientParsing_2100(clientMessage)));
        response.addProperty("ServerResponse", serverResponse);
        response.add("serverResponseParsing", serverResponseParsing);

        String formattedJson = new GsonBuilder().setPrettyPrinting().create().toJson(response);
        logger.info("INFO: Response JSON: " + formattedJson);
        return formattedJson;
    }

    public String GetJsonToMsg_2100(String jsonBody) {
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
    
                    // Gunakan ReguestClientParsing_2100 untuk mendapatkan hasil format JSON
                    String formattedJsonOutput = ReguestClientParsing_2100(message);
    
                    // Cetak JSON terformat di log
                    logger.info(" - Send Request Body Parsing Type 1: " + formattedJsonOutput);
                }
            } else if (typeMessage == 2) {
                // Mengambil setiap field dengan nilai default jika field tidak ada
                String mti          = inputObject.has("mti") ? inputObject.get("mti").getAsString() : "";
                String bit_map      = inputObject.has("bit_map") ? inputObject.get("bit_map").getAsString() : "";
                String pan_len      = inputObject.has("pan_len") ? inputObject.get("pan_len").getAsString() : "";
                String pan          = inputObject.has("pan") ? inputObject.get("pan").getAsString() : "";
                String switcher_tan = inputObject.has("switcher_tan") ? inputObject.get("switcher_tan").getAsString() : "";
                String dtl_trans    = inputObject.has("dtl_trans") ? inputObject.get("dtl_trans").getAsString() : "";
                String merchant     = inputObject.has("merchant") ? inputObject.get("merchant").getAsString() : "";
                String bank_len     = inputObject.has("bank_len") ? inputObject.get("bank_len").getAsString() : "";
                String bank_id      = inputObject.has("bank_id") ? inputObject.get("bank_id").getAsString() : "";
                String apd_len      = inputObject.has("apd_len") ? inputObject.get("apd_len").getAsString() : "";
                String switcher_id  = inputObject.has("switcher_id") ? inputObject.get("switcher_id").getAsString() : "";
                String register_num = inputObject.has("register_num") ? inputObject.get("register_num").getAsString() : "";
                String trans_code   = inputObject.has("trans_code") ? inputObject.get("trans_code").getAsString() : "";
    
                message = mti          + bit_map      + pan_len      + pan          + switcher_tan +
                          dtl_trans    + merchant     + bank_len     + bank_id      + apd_len      +
                          switcher_id  + register_num + trans_code;
    
                // Menyusun JSON sesuai format
                jsonObjectResponse.addProperty("mti",mti);
                jsonObjectResponse.addProperty("bit_map",bit_map);
                jsonObjectResponse.addProperty("pan_len",pan_len);
                jsonObjectResponse.addProperty("pan",pan);
                jsonObjectResponse.addProperty("switcher_tan",switcher_tan);
                jsonObjectResponse.addProperty("dtl_trans",dtl_trans);
                jsonObjectResponse.addProperty("merchant",merchant);
                jsonObjectResponse.addProperty("bank_len",bank_len);
                jsonObjectResponse.addProperty("bank_id",bank_id);
                jsonObjectResponse.addProperty("apd_len",apd_len);
                jsonObjectResponse.addProperty("switcher_id",switcher_id);
                jsonObjectResponse.addProperty("register_num",register_num);
                jsonObjectResponse.addProperty("trans_code",trans_code);
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
    // Parsing Request Client 2100
    //-----------------------------------------------------------------
    public static String ReguestClientParsing_2100(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
    
        try {
            // Daftar nama field
            String[] fields = {
                               "mti", "bit_map", "pan_len", "pan", "switcher_tan", "dtl_trans", "merchant",
                               "bank_len", "bank_id", "apd_len", "switcher_id", "register_num", "trans_code"
                              };
    
            // Panjang masing-masing field
            int[] fieldLengths = {4, 16, 2, 5, 12, 14, 4, 2, 7, 3, 7, 13, 3};
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
    // Parsing respon server netman 2110
    //-----------------------------------------------------------------
    public static String ResponServerParsing_2110(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
        String rcValue = "0000";

        try {
            // Daftar nama field
            String[] fields = {
                "mti", "bitMap", "pan_len", "pan", "ISOCurrCode", "CurrMinorUnit", "CurrValueAmount",
                "switcher_tan", "dtl_trans", "merchant", "bank_len", "bank_id", "RC", "apd_Len", "switcher_id",
                "register_num", "trans_code", "trans_name", "register_date", "register_expiredate", "Subscriber_id",
                "Subscriber_name", "refnunum_srv", "service_unit", "service_unitaddress", "service_unitphone",
                "trans_amount_minorunit", "trans_amount", "servbill_minorunit", "servbill_value",
                "servbill_ChargeAdminMinorUnit", "servbill_ChargeAdminValue", "apd2_Len", "apd2_BillCompType",
                "apd2_BillCompMinorunit", "apd2_BillCompValueAmount"
            };

            // Panjang masing-masing field
            int[] fieldLengths = {
                4, 16, 2, 5, 3, 1, 12, 12, 14, 4, 2, 7, 4, 3, 7, 13,
                3, 25, 8, 8, 12, 25, 32, 5, 35, 15, 1, 17, 1, 17,
                1, 10, 3, 2, 1, 19
            };

            int currentIndex = 0;
            // Parsing data awal
            for (int i = 0; i < fields.length; i++) {
                if (inputMessage.length() >= currentIndex + fieldLengths[i]) {
                    String value = inputMessage.substring(currentIndex, currentIndex + fieldLengths[i]);
                    parsedObject.addProperty(fields[i], value);

                    if (fields[i].equals("RC")) {
                        rcValue = value;
                    }
                    currentIndex += fieldLengths[i];

                    if (rcValue.equals("0048") && fields[i].equals("register_expiredate")) {
                        break;
                    }

                    if (rcValue.equals("0088") && fields[i].equals("refnunum_srv")) {
                        // Jika ditemukan RC = '0088', ambil sisa message setelah currentIndex
                        String sisaMessage = inputMessage.substring(currentIndex);
                        if (sisaMessage.length() >= 3) {
                            String infoTextLen = sisaMessage.substring(0, 3);
                            String infoText = sisaMessage.substring(3);
                            parsedObject.addProperty("info_text_len", infoTextLen);
                            parsedObject.addProperty("info_text", infoText);
                        }
                        break;
                    }
                } else {
                    parsedObject.addProperty(fields[i], "");
                    break;
                }
            }

            jsonArray.add(parsedObject);
        } catch (Exception e) {
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("status", "ERROR");
            errorObject.addProperty("message", "Terjadi kesalahan saat memproses inputMessage: " + e.getMessage());
            jsonArray.add(errorObject);
        }

        return new Gson().toJson(jsonArray);
    }
}
