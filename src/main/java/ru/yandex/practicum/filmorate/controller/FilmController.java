package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.toString());
        return new ResponseEntity<>(filmService.addFilm(film), HttpStatus.OK);
    }

    @PutMapping("/films")
    public ResponseEntity<Object> changeFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films: {}", film.toString());
        return new ResponseEntity<>(filmService.changeFilm(film), HttpStatus.OK);
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(filmService.getFilms()));
    }

}
