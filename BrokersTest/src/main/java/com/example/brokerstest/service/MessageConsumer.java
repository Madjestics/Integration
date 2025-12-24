package com.example.brokerstest.service;

import com.example.brokerstest.entity.UserPreferences;
import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.commontest.dto.MovieDto;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final UserPreferencesRepository preferencesRepository;

    protected void handleMovieEvent(MovieEvent event) {
        switch (event.getAction()) {
            case ADD -> updatePreferencesForUsersWithNewMovie(event.getMovie());
            case UPDATE -> updatePreferencesForUsersWithUpdatedMovie(event.getMovie());
            case DELETE -> updatePreferencesForUsersWithDeletedMovie(event.getMovie());
            default -> log.error("Неподдерживаемый тип действия над фильмом");
        }
    }

    protected void handleWatchEvent(WatchEvent event) {
        log.info("Обработка события просмотра фильма: " + event);
        updateUserPreferencesWhenWatchedMovie(event);
    }

    // Обновление пользовательских предпочтений с учетом нового фильма
    protected void updatePreferencesForUsersWithNewMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом нового фильма");
        preferencesRepository.findAll()
                .forEach(preferences -> {
                    if (preferences.getPreferredGenres().contains(movie.getGenre()) &&
                            !preferences.getViewedMovies().contains(movie)) {
                        // Добавляем фильм в список рекомендаций пользователя
                        if (!preferences.getRecommendedMovies().contains(movie)) {
                            preferences.getRecommendedMovies().add(movie);
                        }
                    }
                    preferencesRepository.save(preferences);
                });
    }

    protected void updatePreferencesForUsersWithUpdatedMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом новых данных фильма");
        preferencesRepository.findAll()
                .forEach(preferences -> {
                    var isMovieViewed = preferences.getViewedMovies().stream()
                            .anyMatch(existedMovie -> existedMovie.getId().equals(movie.getId()));

                    preferences.getRecommendedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    preferences.getViewedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));

                    if (isMovieViewed) {
                        preferences.getViewedMovies().add(movie);
                    } else {
                        preferences.getRecommendedMovies().add(movie);
                    }
                    preferencesRepository.save(preferences);
                });
    }

    protected void updatePreferencesForUsersWithDeletedMovie(MovieDto movie) {
        log.info("Обновление пользовательских предпочтений с учетом удаления фильма");
        preferencesRepository.findAll()
                .forEach(preferences -> {
                    preferences.getRecommendedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    preferences.getViewedMovies().removeIf(existingMovie -> existingMovie.getId().equals(movie.getId()));
                    preferencesRepository.save(preferences);
                });
    }

    protected void updateUserPreferencesWhenWatchedMovie(WatchEvent event) {
        log.info("Обновление пользовательских предпочтений с учетом просмотра фильма");
        UserPreferences userPreferences = preferencesRepository.findByUserId(event.getUserId());
        MovieDto movie = event.getMovie();
        if (!userPreferences.getViewedMovies().contains(movie)) {
            userPreferences.getViewedMovies().add(movie);
            String movieGenre = movie.getGenre();
            List<MovieDto> movieWithSameGenre = userPreferences.getViewedMovies().stream()
                    .parallel()
                    .filter(movieDto -> movieDto.getGenre().equals(movieGenre))
                    .toList();
            if (movieWithSameGenre.size()>=3 && !userPreferences.getPreferredGenres().contains(movieGenre)) {
                userPreferences.getPreferredGenres().add(movieGenre);
            }
        }
        userPreferences.getRecommendedMovies().removeIf(m -> Objects.equals(m.getId(), movie.getId()));
        preferencesRepository.save(userPreferences);
    }
}
