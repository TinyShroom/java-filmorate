package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public void putLike(Long id, Long userId) {
        var film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException(String.format("PUT like: film id %d not found", id));
        }
        var user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("PUT like: user id %d not found", userId));
        }
        film.getLikes().add(userId);
    }

    public void deleteLike(Long id, Long userId) {
        var film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException(String.format("PUT like: film id %d not found", id));
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {
        if (count < 1) {
            throw new ValidationException("GET popular: count must be greater than 0");
        }
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long idGenerator() {
        return idCounter++;
    }

}
