package com.example.moviestest.mapper;

import com.example.moviestest.dto.UserDto;
import com.example.moviestest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto map(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isEnabled(),
                user.getCreatedAt(), user.getUpdatedAt());
    }

    public User map(UserDto userDto) {
        return new User(userDto.getId(), userDto.getUsername(), userDto.getPassword(), userDto.getRole(),
                userDto.isEnabled(), userDto.getCreatedAt(), userDto.getUpdatedAt());
    }
}
