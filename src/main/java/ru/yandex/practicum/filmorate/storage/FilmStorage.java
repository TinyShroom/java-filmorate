package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> findAll();

    Optional<Film> findById(Long id);

    void putLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getPopular(int count);
}
