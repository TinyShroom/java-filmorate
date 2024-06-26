package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        return filmStorage.update(film)
                .orElseThrow(() -> new NotFoundException(String.format("film with id %d not found", film.getId()))
        );
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("film with id %d not found", id))
        );
    }

    @Override
    public void putLike(Long id, Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("PUT like: user id %d not found", userId))
        );
        filmStorage.putLike(id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        filmStorage.deleteLike(id, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
