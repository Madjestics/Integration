package com.example.moviestest.repository;

import com.example.moviestest.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Director findByFio(String fio);
}
