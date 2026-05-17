package com.example.Human_Resource_Managment.ExceptionHandling;

public class JobHistoryException extends RuntimeException {
    
    public JobHistoryException(String message) {
        super(message);
    }
    
    public JobHistoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
