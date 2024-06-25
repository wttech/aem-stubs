package com.wttech.aem.stubs.core;

public class StubResponseException extends StubException {

    public StubResponseException(String message, Exception e) {
        super(message, e);
    }

    public StubResponseException(String message) {
        super(message);
    }
}
