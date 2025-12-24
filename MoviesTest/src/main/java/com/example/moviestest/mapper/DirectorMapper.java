package com.example.moviestest.mapper;

import com.example.moviestest.dto.DirectorDto;
import com.example.moviestest.entity.Director;
import com.example.moviestest.entity.Movie;
import com.example.moviestest.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorMapper {
    private final MovieRepository movieRepository;

    public DirectorDto map(Director director) {
        List<String> movies = movieRepository.findMoviesByDirectorId(director.getId()).stream()
                .map(Movie::getTitle).toList();
        return new DirectorDto(director.getId(), director.getFio(), movies);
    }

    public Director map(DirectorDto dto) {
        return new Director(dto.getId(), dto.getFio());
    }
}
