package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingMpaService {

    private final RatingMpaStorage ratingMpaStorage;

    public RatingMpa getById(int id) {
        return ratingMpaStorage.findById(id);
    }

    public Collection<RatingMpa> getAll() {
        return ratingMpaStorage.findAll();
    }
}
