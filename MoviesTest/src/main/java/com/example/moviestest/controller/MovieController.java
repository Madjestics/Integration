package com.example.moviestest.controller;

import com.example.commontest.dto.MovieDto;
import com.example.moviestest.dto.VideoStreamData;
import com.example.moviestest.entity.Movie;
import com.example.moviestest.mapper.MovieMapper;
import com.example.moviestest.service.MovieService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movie")
public class MovieController {
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<MovieDto> findAll() {
        return movieService.findAll()
                .stream().map(movieMapper::map).toList();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public MovieDto findById(@PathVariable(name = "id") Long id) {
        return movieMapper.map(movieService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDto addMovie(@RequestBody MovieDto dto) {
        Movie saved = movieService.add(movieMapper.map(dto));
        return movieMapper.map(saved);
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public MovieDto updateMovie(@RequestBody MovieDto dto, @PathVariable Long id) {
        Movie updated = movieService.update(movieMapper.map(dto), id);
        return movieMapper.map(updated);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
    }

    @PostMapping("upload/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void uploadMovie(@RequestPart("movie") MultipartFile multipartFile, @PathVariable Long id) {
        movieService.uploadMovie(multipartFile, id);
    }

    @GetMapping(value = "/watch/{id}", produces = "video/mp4")
    public void watchMovie(@PathVariable Long id,
                           @RequestHeader(value = "Range", required = false) String rangeHeader,
                           HttpServletResponse response) {
        VideoStreamData streamData = movieService.getForWatching(id, rangeHeader);

        response.setStatus(rangeHeader != null ? 206 : 200);
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + streamData.start() + "-" + streamData.end() + "/" + streamData.fileLength());
        response.setContentLengthLong(streamData.contentLength());

        try (InputStream is = streamData.inputStream();
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
