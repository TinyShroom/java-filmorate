package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.toString());
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films: {}", film.toString());
        return filmService.changeFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET /films: all");
        return new ArrayList<>(filmService.getFilms());
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("GET /film: {}", id);
        return filmService.getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
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
