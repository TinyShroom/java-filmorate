package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingMpaStorage {

    List<Mpa> findAll();

    Mpa findById(Integer id);
}
