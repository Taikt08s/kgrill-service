package com.swd392.group2.kgrill_service.exception;

public class OrderDetailNotFoundException extends RuntimeException{
    public OrderDetailNotFoundException(String message){
        super(message);
    }
}
