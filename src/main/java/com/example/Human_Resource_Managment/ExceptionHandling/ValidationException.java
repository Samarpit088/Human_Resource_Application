package com.example.Human_Resource_Managment.ExceptionHandling;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
}
