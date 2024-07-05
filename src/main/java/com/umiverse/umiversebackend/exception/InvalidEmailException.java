package com.umiverse.umiversebackend.exception;

public class InvalidEmailException extends Exception {
    public InvalidEmailException(){
        super("Invalid email address");
    }
}