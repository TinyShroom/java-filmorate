package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Integer id) {
        log.info("GET /genres: {}", id);
        var genre = genreService.getById(id);
        log.info("completion GET /genres: {}", genre);
        return genre;
    }

    @GetMapping
    public List<Genre> getAll() {
        log.info("GET /genres: all");
        var genres = genreService.getAll();
        log.info("completion GET /genres: size {}", genres.size());
        return genres;
    }
}
