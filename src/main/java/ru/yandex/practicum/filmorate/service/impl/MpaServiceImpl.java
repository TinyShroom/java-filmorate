package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public Mpa getById(int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("mpa with id == %d not found", id))
        );
    }

    @Override
    public List<Mpa> getAll() {
        return mpaStorage.findAll();
    }
}