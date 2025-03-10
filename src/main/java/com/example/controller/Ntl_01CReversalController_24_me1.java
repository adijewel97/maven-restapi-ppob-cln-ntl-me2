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
public class Ntl_01CReversalController_24_me1 {

    // Mendapatkan logger dari LoggerUtil
    private static final Logger logger = LoggerUtil.getLogger(Ntl_01BInqueryController_21_me1.class);

    //-----------------------------------------------------------------
    //1. ppob signin
    //-----------------------------------------------------------------
    @POST
    @Path("/post_ppob_reversal_me")
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
            message = GetJsonToMsg_2400(jsonBody);
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
                serverResponseArrayParsing = JsonParser.parseString(ResponServerParsing_2410(serverResponse)).getAsJsonArray();
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
        response.add("ClientInqueryParsing", JsonParser.parseString(ReguestClientParsing_2400(clientMessage)));
        response.addProperty("ServerResponse", serverResponse);
        response.add("serverResponseParsing", serverResponseParsing);

        String formattedJson = new GsonBuilder().setPrettyPrinting().create().toJson(response);
        logger.info("INFO: Response JSON: " + formattedJson);
        return formattedJson;
    }

    public String GetJsonToMsg_2400(String jsonBody) {
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
                if (!inputObject.has("message") || inputObject.get("message").getAsString().isEmpty()) {
                    message = "";
                } else {
                    message = inputObject.get("message").getAsString();
                    String formattedJsonOutput = ReguestClientParsing_2400(message);
                    logger.info(" - Send Request Body Parsing Type 1: " + formattedJsonOutput);
                }
            } else if (typeMessage == 2) {
                StringBuilder sb = new StringBuilder();

                String mti                           = inputObject.has("mti")                           ? inputObject.get("mti").getAsString() : "";
                String bit_map                       = inputObject.has("bit_map")                       ? inputObject.get("bit_map").getAsString() : "";
                String pan_len                       = inputObject.has("pan_len")                       ? inputObject.get("pan_len").getAsString() : "";
                String pan                           = inputObject.has("pan")                           ? inputObject.get("pan").getAsString() : "";
                String CurrISOCode                   = inputObject.has("CurrISOCode")                   ? inputObject.get("CurrISOCode").getAsString() : "";
                String CurrMinorUnit                 = inputObject.has("CurrMinorUnit")                 ? inputObject.get("CurrMinorUnit").getAsString() : "";
                String CurrValueAmount               = inputObject.has("CurrValueAmount")               ? inputObject.get("CurrValueAmount").getAsString() : "";
                String switcher_tan                  = inputObject.has("switcher_tan")                  ? inputObject.get("switcher_tan").getAsString() : "";
                String dtl_trans                     = inputObject.has("dtl_trans")                     ? inputObject.get("dtl_trans").getAsString() : "";
                String merchant                      = inputObject.has("merchant")                      ? inputObject.get("merchant").getAsString() : "";
                String bank_len                      = inputObject.has("bank_len")                      ? inputObject.get("bank_len").getAsString() : "";
                String bank_id                       = inputObject.has("bank_id")                       ? inputObject.get("bank_id").getAsString() : "";
                String apd_len                       = inputObject.has("apd_len")                       ? inputObject.get("apd_len").getAsString() : "267";
                String switcher_id                   = inputObject.has("switcher_id")                   ? inputObject.get("switcher_id").getAsString() : "";
                String register_num                  = inputObject.has("register_num")                  ? inputObject.get("register_num").getAsString() : "";
                String trans_code                    = inputObject.has("trans_code")                    ? inputObject.get("trans_code").getAsString() : "";
                String trans_name                    = inputObject.has("trans_name")                    ? inputObject.get("trans_name").getAsString() : "";
                String register_date                 = inputObject.has("register_date")                 ? inputObject.get("register_date").getAsString() : "";
                String register_expiredate           = inputObject.has("register_expiredate")           ? inputObject.get("register_expiredate").getAsString() : "";
                String Subscriber_id                 = inputObject.has("Subscriber_id")                 ? inputObject.get("Subscriber_id").getAsString() : "";
                String Subscriber_name               = inputObject.has("Subscriber_name")               ? inputObject.get("Subscriber_name").getAsString() : "";
                String refnum_svr                    = inputObject.has("refnum_svr")                    ? inputObject.get("refnum_svr").getAsString() : "";
                String refnum_switcher               = inputObject.has("refnum_switcher")               ? inputObject.get("refnum_switcher").getAsString() : "";
                String service_unit                  = inputObject.has("service_unit")                  ? inputObject.get("service_unit").getAsString() : "";
                String service_unitaddress           = inputObject.has("service_unitaddress")           ? inputObject.get("service_unitaddress").getAsString() : "";
                String service_unitphone             = inputObject.has("service_unitphone")             ? inputObject.get("service_unitphone").getAsString() : "";
                String trans_amount_minorunit        = inputObject.has("trans_amount_minorunit")        ? inputObject.get("trans_amount_minorunit").getAsString() : "";
                String trans_amount                  = inputObject.has("trans_amount")                  ? inputObject.get("trans_amount").getAsString() : "";
                String servbill_minorunit            = inputObject.has("servbill_minorunit")            ? inputObject.get("servbill_minorunit").getAsString() : "";
                String servbill_value                = inputObject.has("servbill_value")                ? inputObject.get("servbill_value").getAsString() : "";
                String servbill_ChargeAdminMinorUnit = inputObject.has("servbill_ChargeAdminMinorUnit") ? inputObject.get("servbill_ChargeAdminMinorUnit").getAsString() : "";
                String servbill_ChargeAdminValue     = inputObject.has("servbill_ChargeAdminValue")     ? inputObject.get("servbill_ChargeAdminValue").getAsString() : "";
                String apd_len_2200                  = inputObject.has("apd_len_2200")                  ? inputObject.get("apd_len_2200").getAsString() : "";
                String mti_2200                      = inputObject.has("mti_2200")                      ? inputObject.get("mti_2200").getAsString() : "";
                String switcher_tan_2200             = inputObject.has("switcher_tan_2200")             ? inputObject.get("switcher_tan_2200").getAsString() : "";
                String date_settlement_2200          = inputObject.has("date_settlement_2200")          ? inputObject.get("date_settlement_2200").getAsString() : "";
                String bank_id_2200                  = inputObject.has("bank_id_2200")                  ? inputObject.get("bank_id_2200").getAsString() : "";
                String apd2_len                      = inputObject.has("apd2_len")                      ? inputObject.get("apd2_len").getAsString() : "";
                String apd2_BillCompType             = inputObject.has("apd2_BillCompType")             ? inputObject.get("apd2_BillCompType").getAsString() : "";
                String apd2_BillCompMinorunit        = inputObject.has("apd2_BillCompMinorunit")        ? inputObject.get("apd2_BillCompMinorunit").getAsString() : "";
                String apd2_BillCompValue            = inputObject.has("apd2_BillCompValue")            ? inputObject.get("apd2_BillCompValue").getAsString() : "";

                sb.append(mti).append(bit_map).append(pan_len).append(pan).append(CurrISOCode)
                .append(CurrMinorUnit).append(CurrValueAmount)
                .append(switcher_tan).append(dtl_trans).append(merchant)
                .append(bank_len).append(bank_id).append(apd_len).append(switcher_id)
                .append(register_num).append(trans_code).append(trans_name).append(register_date)
                .append(register_expiredate).append(Subscriber_id)
                .append(Subscriber_name).append(refnum_svr).append(refnum_switcher).append(service_unit)
                .append(service_unitaddress).append(service_unitphone)
                .append(trans_amount_minorunit).append(trans_amount).append(servbill_minorunit)
                .append(servbill_value).append(servbill_ChargeAdminMinorUnit).append(servbill_ChargeAdminValue)
                .append(apd_len_2200).append(mti_2200).append(switcher_tan_2200).append(date_settlement_2200)
                .append(bank_id_2200).append(apd2_len).append(apd2_BillCompType)
                .append(apd2_BillCompMinorunit).append(apd2_BillCompValue);

                message = sb.toString();

                jsonObjectResponse.addProperty("message", message);
                jsonArrayResponse.add(jsonObjectResponse);

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
    public static String ReguestClientParsing_2400(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
    
        try {
            // Daftar nama field
            String[] fields = {
                                "mti", "bit_map", "pan_len", "pan", "CurrISOCode", "CurrMinorUnit", "CurrValueAmount",
                                "switcher_tan", "dtl_trans", "merchant", "bank_len", "bank_id", "apd_len", "switcher_id",
                                "register_num", "trans_code", "trans_name", "register_date", "register_expiredate",
                                "Subscriber_id", "Subscriber_name", "refnum_svr", "refnum_switcher", "service_unit",
                                "service_unitaddress", "service_unitphone", "trans_amount_minorunit", "trans_amount",
                                "servbill_minorunit", "servbill_value", "servbill_ChargeAdminMinorUnit", "servbill_ChargeAdminValue",
                                "apd_len_2200", "mti_2200", "switcher_tan_2200", "date_settlement_2200", "bank_id_2200",
                                "apd2_len", "apd2_BillCompType", "apd2_BillCompMinorunit", "apd2_BillCompValue"
                              };
    
            // Panjang masing-masing field
            int[] fieldLengths = {
                                    4, 16, 2, 5, 3, 1, 12, 12, 14, 4, 2, 7, 3, 7, 13, 3, 25, 8, 8, 12, 25, 
                                    32, 32, 5, 35, 15, 1, 17, 1, 17, 1, 10, 2, 4, 12, 14, 7, 3, 2, 1, 19
                              };
            int currentIndex = 0;
    
            // Parsing data awal
            for (int i = 0; i < fields.length; i++) {
                if (inputMessage.length() >= currentIndex + fieldLengths[i]) {
                    String value = inputMessage.substring(currentIndex, currentIndex + fieldLengths[i]);
                    parsedObject.addProperty(fields[i], value);
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
    // Parsing respon server netman 2410
    //-----------------------------------------------------------------
    public static String ResponServerParsing_2410(String inputMessage) {
        JsonArray jsonArray = new JsonArray();
        JsonObject parsedObject = new JsonObject();
    
        try {
            // Daftar nama field
            String[] fields = {
                "mti", "bitMap", "pan_len", "pan", "CurrISOCode", "CurrMinorUnit", "CurrValueAmount", "switcher_tan", "dtl_trans",
                "merchant", "bank_len", "bank_id", "rc", "apd_len", "switcher_id", "register_num", "trans_code", "trans_name",
                "register_date", "register_expiredate", "subscriber_id", "subscriber_name", "refnum_svr", "refnum_switcher",
                "service_unit", "service_unitaddress", "service_unitphone", "trans_amount_minorunit", "trans_amount",
                "servbill_minorunit", "servbill_value", "servbill_ChargeAdminMinorUnit", "servbill_ChargeAdminValue",
                "apd_len_2200", "mti_2200", "switcher_tan_2200", "date_settlement_2200", "bank_id_2200", "apd2_len",
                "apd2_BillCompType", "apd2_BillCompMinorunit", "apd2_BillCompValue"
            };
    
            // Panjang masing-masing field
            int[] fieldLengths = {
                    4, 16, 2, 5, 3, 1, 12, 12, 14, 4, 2, 7, 4, 3, 7, 13, 3, 25, 8, 8, 12, 25, 32, 32,
                    5, 35, 15, 1, 17, 1, 17, 1, 10, 2, 4, 12, 14, 7, 3, 2, 1,19
            };
    
            int currentIndex = 0;
            
            // Parsing data awal
            for (int i = 0; i < fields.length; i++) {
                if (inputMessage.length() >= currentIndex + fieldLengths[i]) {
                    String value = inputMessage.substring(currentIndex, currentIndex + fieldLengths[i]);
                    parsedObject.addProperty(fields[i], value);
                    
                    currentIndex += fieldLengths[i];
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
