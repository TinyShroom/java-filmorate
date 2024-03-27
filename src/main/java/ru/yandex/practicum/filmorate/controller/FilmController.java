package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.toString());
        return new ResponseEntity<>(filmService.addFilm(film), HttpStatus.CREATED);
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

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("GET /film: {}", id);
        return filmService.getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /like: {}, {}", id, userId);
        filmService.putLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /like: {}, {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /popular: {}", count);
        return filmService.getPopular(count);
    }
}
