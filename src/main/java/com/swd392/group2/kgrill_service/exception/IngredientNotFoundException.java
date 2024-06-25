package com.swd392.group2.kgrill_service.exception;

public class IngredientNotFoundException extends RuntimeException{
    private static final long serialVersionUID= 1;

    public IngredientNotFoundException(String message) {
        super(message);
    }
}
