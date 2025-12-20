package com.example.moviestest.service.impl;

import com.example.commontest.dto.Action;
import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;
import com.example.moviestest.dto.VideoStreamData;
import com.example.moviestest.entity.Movie;
import com.example.moviestest.exception.EntityNotFoundException;
import com.example.moviestest.exception.ValidationException;
import com.example.moviestest.mapper.MovieMapper;
import com.example.moviestest.repository.DirectorRepository;
import com.example.moviestest.repository.MovieRepository;
import com.example.moviestest.security.CustomPrincipal;
import com.example.moviestest.service.MessageService;
import com.example.moviestest.service.MovieService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final MessageService messageService;
    private final MovieMapper movieMapper;
    private final MinioClient minioClient;

    @Value("${storage.bucket}")
    private String moviesBucket;

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        if (id == null) throw new ValidationException("ID не может быть пустым");
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм не найден"));
    }

    @Override
    public Movie add(Movie movie) {
        validate(movie);
        Movie saved = movieRepository.save(movie);
        messageService.sendMovieEvent(new MovieEvent(Action.ADD, movieMapper.map(saved)));
        return saved;
    }

    @Override
    public Movie update(Movie movie, Long id) {
        validate(movie);
        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм для обновления не найден"));
        movie.setFilePath(existing.getFilePath());
        Movie saved = movieRepository.save(movie);
        messageService.sendMovieEvent(new MovieEvent(Action.UPDATE, movieMapper.map(saved)));
        return saved;
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new ValidationException("Недопустимый ID");
        Movie existing = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм для удаления не найден"));
        movieRepository.deleteById(id);
        messageService.sendMovieEvent(new MovieEvent(Action.DELETE, movieMapper.map(existing)));
    }

    @Override
    public Long uploadMovie(MultipartFile file, Long id) {
        try {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Фильм не найден"));
            String objectName = "movies/" + id + "_" + file.getOriginalFilename();
            log.info("Начал загрузку");
            try (InputStream is = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(moviesBucket)
                                .object(objectName)
                                .stream(is, file.getSize(), 10*1024*1024)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            log.info("Завершил загрузку");
            LocalTime duration = getMovieDuration(file);
            movie.setDuration(duration);
            movie.setFilePath(objectName);
            movieRepository.save(movie);

            return id;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке фильма", e);
        }
    }

    @Override
    public VideoStreamData getForWatching(Long id, String rangeHeader) {
        try {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Фильм не найден"));

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                CustomPrincipal principal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                // Отправляем WatchEvent
                messageService.sendWatchEvent(new WatchEvent(movieMapper.map(movie), principal.getId()));
            }

            String objectName = movie.getFilePath();

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(moviesBucket).object(objectName).build()
            );
            long fileSize = stat.size();

            long rangeStart = 0;
            long rangeEnd = fileSize - 1;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                try {
                    rangeStart = Long.parseLong(ranges[0]);
                    if (ranges.length > 1 && !ranges[1].isEmpty()) {
                        rangeEnd = Long.parseLong(ranges[1]);
                    }
                } catch (NumberFormatException ignored) {}
            }

            long contentLength = rangeEnd - rangeStart + 1;

            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(moviesBucket)
                            .object(objectName)
                            .offset(rangeStart)
                            .length(contentLength)
                            .build()
            );

            return new VideoStreamData(inputStream, fileSize, rangeStart, rangeEnd, contentLength);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении фильма", e);
        }
    }

    private void validate(Movie movie) {
        if (movie == null) throw new ValidationException("Фильм не может быть null");
        if (!StringUtils.hasText(movie.getTitle())) throw new ValidationException("Название фильма не может быть пустым");
        if (movie.getTitle().length() > 100) throw new ValidationException("Название фильма должно быть менее 100 символов");
        if (movie.getYear() == null || movie.getYear() < 1900 || movie.getYear() > 2100)
            throw new ValidationException("Год выхода фильма должен быть между 1900 и 2100");
        if (movie.getDuration() == null || movie.getDuration().isBefore(LocalTime.MIN))
            throw new ValidationException("Длительность фильма должна быть больше 0 секунд");
        if (movie.getRating() == null || movie.getRating() < 0 || movie.getRating() > 10)
            throw new ValidationException("Рейтинг фильма должен быть между 0 и 10 баллами");
        if (!StringUtils.hasText(movie.getGenre())) throw new ValidationException("Жанр фильма не может быть пустым");

        if (movie.getDirector() == null)
            throw new ValidationException("Режиссер фильма не может быть пустым");

        if (!directorRepository.existsById(movie.getDirector().getId()))
            throw new EntityNotFoundException("Директор фильма не найден");
    }

    public LocalTime getMovieDuration(MultipartFile file) {
        try {
            File temp = File.createTempFile("video", ".mp4");
            file.transferTo(temp);
            SeekableByteChannel channel = NIOUtils.readableChannel(temp);
            FrameGrab grab = FrameGrab.createFrameGrab(channel);

            double durationSeconds = grab.getVideoTrack().getMeta().getTotalDuration();

            int hours = (int) durationSeconds / 3600;
            int minutes = ((int) durationSeconds % 3600) / 60;
            int seconds = (int) durationSeconds % 60;

            return LocalTime.of(hours, minutes, seconds);
        } catch (IOException | JCodecException e) {
            throw new RuntimeException("Не удалось определить длительность видео", e);
        }
    }
}