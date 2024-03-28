package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("GET /genres: {}", id);
        return genreService.getById(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> getFilms() {
        log.info("GET /genres: all");
        return genreService.getAll();
    }
}
