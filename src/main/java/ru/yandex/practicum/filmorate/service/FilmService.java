package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film changeFilm(Film film) {
        var oldFilm = filmStorage.findById(film.getId());
        if (oldFilm == null) {
            throw new NotFoundException(String.format("POST /films: film with id %d not found ", film.getId()));
        }
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    public Film getFilm(Long id) {
        return filmStorage.findById(id);
    }

    public void putLike(Long id, Long userId) {
        var user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("PUT like: user id %d not found", userId));
        }
        filmStorage.putLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getPopular(int count) {
        if (count < 1) {
            throw new ValidationException("GET popular: count must be greater than 0");
        }
        return filmStorage.getPopular(count);
    }
}
