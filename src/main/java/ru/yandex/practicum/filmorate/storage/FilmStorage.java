package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(Long id);

    void putLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getPopular(int count);
}
