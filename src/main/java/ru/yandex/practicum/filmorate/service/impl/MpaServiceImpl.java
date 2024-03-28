package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final RatingMpaStorage ratingMpaStorage;

    @Override
    public Mpa getById(int id) {
        return ratingMpaStorage.findById(id);
    }

    @Override
    public List<Mpa> getAll() {
        return ratingMpaStorage.findAll();
    }
}
