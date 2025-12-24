package com.example.moviestest.mapper;

import com.example.commontest.dto.MovieDto;
import com.example.moviestest.entity.Director;
import com.example.moviestest.entity.Movie;
import com.example.moviestest.repository.DirectorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieMapper {
    private final DirectorRepository directorRepository;

    public Movie map(MovieDto dto) {
        Director director = directorRepository.findByFio(dto.getDirector());
        return new Movie(
                dto.getId(),
                dto.getTitle(),
                dto.getYear(),
                dto.getDuration(),
                dto.getGenre(),
                director,
                dto.getDescription()
        );
    }

    public MovieDto map(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getDuration(),
                movie.getGenre(),
                movie.getDirector().getFio(),
                movie.getDescription()
        );
    }
}
