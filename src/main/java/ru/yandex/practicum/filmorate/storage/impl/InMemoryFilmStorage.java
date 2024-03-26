package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long idCounter;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        film.setId(idGenerator());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    @Override
    public void putLike(Long id, Long userId) {
        var film = films.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("film id %d not found", id));
        }
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        var film = films.get(id);
        if (film == null) {
            throw new NotFoundException(String.format("PUT like: film id %d not found", id));
        }
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private long idGenerator() {
        return ++idCounter;
    }
}
