package com.umiverse.umiversebackend.exception;

public class AlreadyAvailableEmail extends Exception {
    public AlreadyAvailableEmail(){
        super("Email address already available");
    }
}