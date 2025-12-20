package com.example.moviestest.exception;

/**
 * Выбрасывается, в случае необработанных ошибок
 */
public class InternalServerException extends RuntimeException{
    public InternalServerException(String message) {
        super(message);
    }
}
