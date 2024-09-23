package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Director add(Director director) {
        return directorStorage.create(director);
    }

    @Override
    public Director update(Director director) {
        return directorStorage.update(director)
                .orElseThrow(() -> new NotFoundException(
                        String.format("director with id == %d not found", director.getId()))
                );
    }

    @Override
    public Director getById(long id) {
        return directorStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("director with id == %d not found", id))
                );
    }

    @Override
    public List<Director> getAll() {
        return directorStorage.findAll();
    }

    @Override
    public void delete(long id) {
        if (!isDirectorExist(id)) {
            throw new NotFoundException(String.format("director with id == %d not found", id));
        }
        directorStorage.delete(id);
    }

    private boolean isDirectorExist(long id) {
        return directorStorage.findById(id).isPresent();
    }

}
