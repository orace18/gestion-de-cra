package com.orace.cra.execptions;


public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}