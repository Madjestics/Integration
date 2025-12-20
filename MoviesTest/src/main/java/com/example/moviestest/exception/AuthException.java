package com.example.moviestest.exception;

/**
 * ошибка аутентификации
 */
public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }
}
