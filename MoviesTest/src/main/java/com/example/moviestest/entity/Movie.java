package com.example.moviestest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "movie", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Integer year;

    private LocalTime duration;

    private String genre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "director", referencedColumnName = "id")
    private Director director;

    @Column(name = "filepath")
    private String filePath;

    private String description;

    public Movie(Long id, String title, Integer year, LocalTime duration, String genre, Director director, String description) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.genre = genre;
        this.director = director;
        this.description = description;
    }
}
