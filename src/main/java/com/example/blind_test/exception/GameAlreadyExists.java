package com.example.blind_test.exception;

public class GameAlreadyExists extends Exception{
    public GameAlreadyExists()
    {
        super("Failed ! Game already exists");
    }
}
