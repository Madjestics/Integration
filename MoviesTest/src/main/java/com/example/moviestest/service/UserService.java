package com.example.moviestest.service;

import com.example.moviestest.entity.User;

public interface UserService {
    User register(User user);
    User findById(Long id);
    User findUserByUsername(String username);
}
