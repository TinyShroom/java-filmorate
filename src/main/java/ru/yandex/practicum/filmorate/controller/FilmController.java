package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.utils.SortingConstraint;

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
        log.info("GET /films/{}", id);
        var resultFilm = filmService.getFilm(id);
        log.info("completion GET /films/{}: {}", id, resultFilm);
        return resultFilm;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /film: {}", id);
        filmService.delete(id);
        log.info("completion DELETE /film: {} success", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /like: {}, {}", id, userId);
        filmService.putLike(id, userId);
        log.info("completion PUT /like: success");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /like: {}, {}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("completion DELETE /like: success");
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Min(1) int count,
                                 @RequestParam(required = false) @Min(1) Integer genreId,
                                 @RequestParam(required = false) @Min(1895) Integer year) {
        log.info("GET /popular: {} {} {}", count, genreId, year);
        var resultFilms = filmService.getPopular(count, genreId, year);
        log.info("completion GET /popular: size {}", resultFilms.size());
        return resultFilms;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable Long directorId, @RequestParam @SortingConstraint String sortBy) {
        log.info("GET /director/{directorId}?sortBy: {}, {}", directorId, sortBy);
        var films = filmService.getByDirectorId(directorId, sortBy);
        log.info("completion GET /director/{directorId}?sortBy: {}, {} success", directorId, sortBy);
        return films;
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("GET /films/search?query={}, by={}", query, by);
        var films = filmService.searchFilms(query, by);
        log.info("completion GET /films/search: size {}", films.size());
        return films;
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommon(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("GET /common: userId={}, friendId={}", userId, friendId);
        var resultFilms = filmService.getCommon(userId, friendId);
        log.info("completion GET /common: userId={}, friendId={}, size={}", userId, friendId, resultFilms.size());
        return resultFilms;
    }
}