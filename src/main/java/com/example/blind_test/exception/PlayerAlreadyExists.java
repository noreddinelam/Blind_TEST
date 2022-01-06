package com.example.blind_test.exception;

public class PlayerAlreadyExists extends Exception{
    public PlayerAlreadyExists() {
        super("FAIL ! Player already exists");
    }
}
