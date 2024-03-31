package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public Genre getById(int id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("genre with id == %d not found", id))
        );
    }

    @Override
    public List<Genre> getAll() {
        return genreStorage.findAll();
    }
}
