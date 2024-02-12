package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@RestController
public class FilmController {

    private final Map<Long, Film> films;
    private long idCounter = 1;

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films: {}", film.toString());
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("POST /films: invalid release date " + film.getReleaseDate());
        }
        if (film.getId() < 1) {
            film.setId(idGenerator(films.keySet()));
        }
        films.put(film.getId(), film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping("/films")
    public ResponseEntity<Void> changeFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films: {}", film.toString());
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("PUT /films: invalid release date " + film.getReleaseDate());
        }
        var oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BeanUtils.copyProperties(film, oldFilm, "id");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(films.values()));
    }

    private long idGenerator(Set<Long> idSet) {
        while (idSet.contains(idCounter)) {
            ++idCounter;
        }
        return idCounter;
    }
}
