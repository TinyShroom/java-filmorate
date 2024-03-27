package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingMpaController {

    private final RatingMpaService ratingMpaService;

    @GetMapping("/mpa/{id}")
    public RatingMpa getGenreById(@PathVariable Integer id) {
        log.info("GET /mpa: {}", id);
        return ratingMpaService.getById(id);
    }

    @GetMapping("/mpa")
    public Collection<RatingMpa> getFilms() {
        log.info("GET /mpa: all");
        return ratingMpaService.getAll();
    }
}
