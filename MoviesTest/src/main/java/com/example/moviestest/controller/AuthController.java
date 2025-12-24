package com.example.moviestest.controller;

import com.example.moviestest.dto.AuthCredentialsDto;
import com.example.moviestest.dto.AuthResponseDto;
import com.example.moviestest.dto.UserDto;
import com.example.moviestest.entity.User;
import com.example.moviestest.mapper.UserMapper;
import com.example.moviestest.security.CustomPrincipal;
import com.example.moviestest.security.SecurityService;
import com.example.moviestest.security.TokenDetails;
import com.example.moviestest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserDto userDto) {
        User user = userMapper.map(userDto);
        return userMapper.map(userService.register(user));
    }

    @PostMapping("/login")
    public AuthResponseDto login (@RequestBody AuthCredentialsDto credentialsDto) {
        TokenDetails tokenDetails = securityService.authenticate(credentialsDto.getUsername(), credentialsDto.getPassword());
        return new AuthResponseDto(
                tokenDetails.getUserId(),
                tokenDetails.getToken(),
                tokenDetails.getCreatedAt(),
                tokenDetails.getExpiredAt()
        );
    }

    @GetMapping("/info")
    public UserDto getUserInfo(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userMapper.map(userService.findById(principal.getId()));
    }
}
