package com.example.blind_test.exception;

public class DeletePlayerException extends Exception {
    public DeletePlayerException()
    {
        super("Fail ! cannot delete the player from DB");
    }
}
