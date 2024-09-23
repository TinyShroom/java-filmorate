package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;

    @Override
    public Film addFilm(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film changeFilm(Film film) {
        return filmStorage.update(film)
                .orElseThrow(() -> new NotFoundException(String.format("film with id %d not found", film.getId()))
        );
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("film with id %d not found", id))
        );
    }

    @Override
    public void putLike(Long id, Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("PUT like: user id %d not found", userId)));
        filmStorage.putLike(id, userId);
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(id)
                .build());
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", userId)));
        filmStorage.deleteLike(id, userId);
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(id)
                .build());
    }

    @Override
    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    @Override
    public List<Film> getByDirectorId(Long id, String sortBy) {
        if (directorStorage.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("director with id == %d not found", id));
        }
        return filmStorage.getByDirectorId(id, sortBy);
    }

    @Override
    public void delete(Long id) {
        filmStorage.delete(id);
    }

    @Override
    public List<Film> getRecommendation(Long id) {
        Long userWithSimilarLikes = userStorage.findUserWithSimilarLikes(id);
        if (userWithSimilarLikes == null) {
            return new ArrayList<>();
        }
        return filmStorage.getFilmRecommendation(id, userWithSimilarLikes);
    }

    public List<Film> searchFilms(String query, String by) {
        final String DIRECTOR = "director";
        final String TITLE = "title";

        String[] byArray = by.split(",");

        if (byArray.length == 1) {
            if (DIRECTOR.equals(byArray[0])) {
                return filmStorage.findByDirectorName(query);
            } else if (TITLE.equals(byArray[0])) {
                return filmStorage.findByTitle(query);
            }
        } else if (byArray.length == 2) {
            if (DIRECTOR.equals(byArray[0]) || TITLE.equals(byArray[1])) {
                return filmStorage.findByTitleOrDirectorName(query, query);
            } else if (TITLE.equals(byArray[0]) || DIRECTOR.equals(byArray[1])) {
                return filmStorage.findByTitleOrDirectorName(query, query);
            }
        }

        throw new IllegalArgumentException("Invalid 'by' parameter: " + by);
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", userId)));

        return filmStorage.getCommon(userId, friendId);
    }
}
