package com.example.blind_test.shared.communication;

public class Response {
    private final String netCode;
    private final String responseData;
    public Response(String netCode,String responseData){
        this.netCode=netCode;
        this.responseData = responseData;
    }

    public String getNetCode() {
        return netCode;
    }

    public String getResponse() {
        return responseData;
    }
}
