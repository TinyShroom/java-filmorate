package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Director create(Director director);

    Optional<Director> update(Director director);

    List<Director> findAll();

    Optional<Director> findById(Long id);

    void delete(Long id);
}
