package com.swd392.group2.kgrill_service.exception;

public class CategoryNotFoundException extends RuntimeException{
    private static final long serialVersionUID= 1;

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
