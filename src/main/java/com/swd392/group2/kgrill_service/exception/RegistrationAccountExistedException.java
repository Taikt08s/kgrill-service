package com.swd392.group2.kgrill_service.exception;

public class RegistrationAccountExistedException extends RuntimeException {
    public RegistrationAccountExistedException(String message) {
        super(message);
    }
}
