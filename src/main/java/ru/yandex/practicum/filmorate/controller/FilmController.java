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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private final Map<Long, Film> films;

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping("/film")
    public ResponseEntity<Void> addFilm(@Valid @RequestBody Film film) {
        log.info("POST /film: {}", film.toString());
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("POST /film: invalid release date " + film.getReleaseDate());
        }
        films.put(film.getId(), film);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/film")
    public ResponseEntity<Void> changeFilm(@Valid @RequestBody Film film) {
        log.info("PUT /film: {}", film.toString());
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("PUT /film: invalid release date " + film.getReleaseDate());
        }
        var oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BeanUtils.copyProperties(film, oldFilm, "id");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/film")
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(films.values()));
    }

}
