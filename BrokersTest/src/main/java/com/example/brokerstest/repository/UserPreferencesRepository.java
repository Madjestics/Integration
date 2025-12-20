package com.example.brokerstest.repository;

import com.example.brokerstest.entity.UserPreferences;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends MongoRepository<UserPreferences, String> {
    UserPreferences findByUserId(Long userId);
}
