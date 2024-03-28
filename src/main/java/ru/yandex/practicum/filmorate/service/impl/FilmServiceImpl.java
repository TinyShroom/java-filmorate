package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film changeFilm(Film film) {
        var oldFilm = filmStorage.findById(film.getId());
        if (oldFilm == null) {
            throw new NotFoundException(String.format("POST /films: film with id %d not found ", film.getId()));
        }
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.findById(id);
    }

    @Override
    public void putLike(Long id, Long userId) {
        var user = userStorage.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("PUT like: user id %d not found", userId));
        }
        filmStorage.putLike(id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        if (count < 1) {
            throw new ValidationException("GET popular: count must be greater than 0");
        }
        return filmStorage.getPopular(count);
    }
}
