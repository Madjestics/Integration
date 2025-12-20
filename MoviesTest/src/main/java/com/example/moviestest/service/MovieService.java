package com.example.moviestest.service;

import com.example.moviestest.dto.VideoStreamData;
import com.example.moviestest.entity.Movie;
import org.springframework.web.multipart.MultipartFile;

public interface MovieService extends BaseService<Movie>  {
    Long uploadMovie(MultipartFile filePart, Long id);
    VideoStreamData getForWatching(Long id, String rangeHeader);
}
