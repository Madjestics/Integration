package com.example.moviestest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private Long userId;
    private String token;
    private Date issuedAt;
    private Date expiredAt;
}
