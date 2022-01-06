package com.example.blind_test.shared.communication;

public class Request {
    private final String requestData;
    private final String netCode;
    public Request(String netCode,String requestData){
        this.requestData = requestData;
        this.netCode = netCode;
    }

    public String getNetCode() {
        return netCode;
    }

    public String getRequestData() {
        return requestData;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestData='" + requestData + '\'' +
                ", netCode='" + netCode + '\'' +
                '}';
    }
}
