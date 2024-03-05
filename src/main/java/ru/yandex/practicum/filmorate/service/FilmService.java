package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.idCounter = 1;
    }

    private long idCounter;

    public Film addFilm(Film film) {
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("POST /films: release date must be after 1895-12-28");
        }
        if (film.getId() < 1) {
            film.setId(idGenerator());
        }
        return filmStorage.save(film);
    }

    public Film changeFilm(Film film) {
        if (!Validator.isFilmValid(film)) {
            throw new ValidationException("PUT /films: release date must be after 1895-12-28");
        }
        var oldFilm = filmStorage.findById(film.getId());
        if (oldFilm == null) {
            throw new NotFoundException(String.format("POST /films: film with id %d not found ", film.getId()));
        }
        return filmStorage.save(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.findAll();
    }

    private long idGenerator() {
        return idCounter++;
    }

}
