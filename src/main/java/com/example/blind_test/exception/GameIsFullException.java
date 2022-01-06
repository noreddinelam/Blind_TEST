package com.example.blind_test.exception;

public class GameIsFullException extends Exception{
    public GameIsFullException(){
        super("Game is full !");
    }
}
