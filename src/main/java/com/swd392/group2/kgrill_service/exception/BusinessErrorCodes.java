package com.swd392.group2.kgrill_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(400, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(400, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(401, FORBIDDEN, "Account is locked please contact administrator for more information"),
    ACCOUNT_DISABLED(401, FORBIDDEN, "Account is disabled please contact administrator for more information"),
    BAD_CREDENTIALS(401, FORBIDDEN, "Email or Password is incorrect"),
    ;
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }
}
