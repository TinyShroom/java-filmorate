package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.toString());
        var resultFilm = filmService.addFilm(film);
        log.info("completion POST /films: {}", resultFilm);
        return resultFilm;
    }

    @PutMapping
    public Film change(@Valid @RequestBody Film film) {
        log.info("PUT /films: {}", film.toString());
        var resultFilm = filmService.changeFilm(film);
        log.info("completion PUT /films: {}", resultFilm);
        return resultFilm;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET /films: all");
        var resultFilms =  filmService.getFilms();
        log.info("completion GET /films: size {}", resultFilms.size());
        return resultFilms;
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("GET /film: {}", id);
        var resultFilm = filmService.getFilm(id);
        log.info("completion GET /films: {}", resultFilm);
        return resultFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /like: {}, {}", id, userId);
        filmService.putLike(id, userId);
        log.info("completion PUT /like: success");
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /like: {}, {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("completion DELETE /like: success");
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Min(1) int count) {
        log.info("GET /popular: {}", count);
        var resultFilms = filmService.getPopular(count);
        log.info("completion GET /popular: size {}", resultFilms.size());
        return resultFilms;
    }
}
