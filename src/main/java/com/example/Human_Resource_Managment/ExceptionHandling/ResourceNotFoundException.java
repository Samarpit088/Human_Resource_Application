package com.example.Human_Resource_Managment.ExceptionHandling;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
