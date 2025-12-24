package com.example.brokerstest.controller;

import com.example.brokerstest.dto.UserPreferencesDto;
import com.example.brokerstest.entity.UserPreferences;
import com.example.brokerstest.mapper.UserPreferencesMapper;
import com.example.brokerstest.service.RecommendationService;
import com.example.commontest.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserPreferencesMapper userPreferencesMapper;

    @GetMapping("/recommendations")
    public List<MovieDto> getRecommendations(@RequestParam Long userId) {
        return recommendationService.generateRecommendations(userId);
    }

    @GetMapping("/watched")
    public List<MovieDto> getWatchedMovies(@RequestParam Long userId) {
        return recommendationService.getViewedMovies(userId);
    }

    @PostMapping
    public UserPreferencesDto createUserPreferences(@RequestBody UserPreferencesDto preferences) {
        UserPreferences userPreferences = recommendationService.createUserPreferences(userPreferencesMapper.map(preferences));
        return userPreferencesMapper.map(userPreferences);
    }
}
