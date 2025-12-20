package com.example.brokerstest.service;

import com.example.brokerstest.entity.UserPreferences;
import com.example.commontest.dto.MovieDto;

import java.util.List;

public interface RecommendationService {
    UserPreferences createUserPreferences(UserPreferences preferences);

    List<MovieDto> generateRecommendations(Long userId);

    List<MovieDto> getViewedMovies(Long userId);
}
