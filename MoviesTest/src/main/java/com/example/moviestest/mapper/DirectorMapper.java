package com.example.moviestest.mapper;

import com.example.moviestest.dto.DirectorDto;
import com.example.moviestest.entity.Director;
import org.springframework.stereotype.Component;

@Component
public class DirectorMapper {
    public DirectorDto map(Director director) {
        return new DirectorDto(director.getId(), director.getFio());
    }

    public Director map(DirectorDto dto) {
        return new Director(dto.getId(), dto.getFio());
    }
}
