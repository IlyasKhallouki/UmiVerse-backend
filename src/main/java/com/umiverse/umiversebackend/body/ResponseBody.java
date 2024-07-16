package com.umiverse.umiversebackend.body;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBody {
    private String message;
    private int code;

    public ResponseBody(String message, int errorCode) {
        this.message = message;
        this.code = errorCode;
    }
}
