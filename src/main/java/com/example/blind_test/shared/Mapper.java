package com.example.blind_test.shared;

public class Mapper {
    private static final Mapper mapper = new Mapper();

    private Mapper() {
    }

    public static Mapper getMapper() {
        return mapper;
    }

}
