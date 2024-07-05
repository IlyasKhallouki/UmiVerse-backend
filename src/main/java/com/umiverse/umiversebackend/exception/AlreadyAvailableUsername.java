package com.umiverse.umiversebackend.exception;

public class AlreadyAvailableUsername extends Exception {
    public AlreadyAvailableUsername(){
        super("Username already available");
    }
}