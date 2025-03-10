package com.example.model;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    @SerializedName("MTI")
    private String mti;

    @SerializedName("Bit_Map")
    private String bitMap;

    @SerializedName("Date_Time_Local")
    private String dateTimeLocal;

    @SerializedName("Response_Code")
    private String responseCode;

    @SerializedName("Action_Code")
    private String actionCode;

    @SerializedName("Length_Additional_Private_Data")
    private String lengthAdditionalData;

    @SerializedName("Additional_Private_Data")
    private AdditionalPrivateData additionalPrivateData;

    // Getter dan setter
    public String getMti() { return mti; }
    public void setMti(String mti) { this.mti = mti; }

    public String getBitMap() { return bitMap; }
    public void setBitMap(String bitMap) { this.bitMap = bitMap; }

    public String getDateTimeLocal() { return dateTimeLocal; }
    public void setDateTimeLocal(String dateTimeLocal) { this.dateTimeLocal = dateTimeLocal; }

    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }

    public String getActionCode() { return actionCode; }
    public void setActionCode(String actionCode) { this.actionCode = actionCode; }

    public String getLengthAdditionalData() { return lengthAdditionalData; }
    public void setLengthAdditionalData(String lengthAdditionalData) { this.lengthAdditionalData = lengthAdditionalData; }

    public AdditionalPrivateData getAdditionalPrivateData() { return additionalPrivateData; }
    public void setAdditionalPrivateData(AdditionalPrivateData additionalPrivateData) { this.additionalPrivateData = additionalPrivateData; }

    // Kelas tambahan untuk Additional_Private_Data
    public static class AdditionalPrivateData {
        @SerializedName("Switcher_ID")
        private String switcherId;

        // Getter dan setter
        public String getSwitcherId() { return switcherId; }
        public void setSwitcherId(String switcherId) { this.switcherId = switcherId; }
    }
}
