package com.example.brokerstest.service.impl;

import com.example.brokerstest.entity.UserPreferences;
import com.example.brokerstest.repository.UserPreferencesRepository;
import com.example.brokerstest.service.RecommendationService;
import com.example.commontest.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserPreferencesRepository preferencesRepository;

    @Override
    public UserPreferences createUserPreferences(UserPreferences preferences) {
        UserPreferences userPreferences = preferencesRepository.findByUserId(preferences.getUserId());
        if (userPreferences != null && !CollectionUtils.isEmpty(preferences.getPreferredGenres())) {
            userPreferences.getPreferredGenres().addAll(preferences.getPreferredGenres());
            return preferencesRepository.save(userPreferences);
        }
        return preferencesRepository.save(preferences);
    }

    public List<MovieDto> generateRecommendations(Long userId) {
        log.info(String.format("Пользователь %s хочет получить рекомендации по фильмам", userId));
        UserPreferences userPreferences = preferencesRepository.findByUserId(userId);
        return userPreferences.getRecommendedMovies();
    }

    @Override
    public List<MovieDto> getViewedMovies(Long userId) {
        log.info(String.format("Пользователь %s хочет получить просмотренные фильмы", userId));
        UserPreferences userPreferences = preferencesRepository.findByUserId(userId);
        return userPreferences.getViewedMovies();
    }
}
