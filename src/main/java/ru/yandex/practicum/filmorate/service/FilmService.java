package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film changeFilm(Film film);

    List<Film> getFilms();

    Film getFilm(Long id);

    void putLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getPopular(int count);
}
