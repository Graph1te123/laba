// src/main/java/com/example/tournament/exception/ResourceNotFoundException.java
package com.example.tournament.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}