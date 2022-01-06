package com.example.blind_test.shared;

public class Properties {
    public static int PORT = 9999;

    // Database constants
    public static String DATABASE_URL = "jdbc:mysql://localhost:3306/blind_test";
    public static String DATABASE_USER = "root";
    public static String DATABASE_PASSWORD = "admin";
    public static String DATABASE_USE_SSL = "true";
    public static String SERVER_TIME_ZONE = "UTC";

    // Buffer size
    public static int BUFFER_SIZE = 10240;
    private Properties(){}
}
