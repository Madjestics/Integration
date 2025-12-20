package com.example.moviestest.exception;

/**
 * ошибка когда не найдена сущность
 */
public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
}
