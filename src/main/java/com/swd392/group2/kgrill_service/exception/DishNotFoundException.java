package com.swd392.group2.kgrill_service.exception;

public class DishNotFoundException extends RuntimeException{
    private static final long serialVersionUID= 1;

    public DishNotFoundException(String message) {
        super(message);
    }
}
