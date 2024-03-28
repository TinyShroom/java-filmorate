package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private User user;
    private Film film;
    private Film filmWithoutGenre;
    private Film filmWithoutMpa;
    private Film filmWithoutAll;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    private static final Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Комедия"),
            2, new Genre(2, "Драма"),
            3, new Genre(3, "Мультфильм"),
            4, new Genre(4, "Триллер"),
            5, new Genre(5, "Документальный"),
            6, new Genre(6, "Боевик")
    );

    private static final Map<Integer, RatingMpa> ratings = Map.of(
            1, new RatingMpa(1, "G"),
            2, new RatingMpa(2, "PG"),
            3, new RatingMpa(3, "PG-13"),
            4, new RatingMpa(4, "R"),
            5, new RatingMpa(5, "NC-17")
    );

    @BeforeEach
    public void init() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
        userDbStorage = new UserDbStorage(jdbcTemplate);

        film = new Film(1, "film_name", "film_description",
                LocalDate.of(2000, 5, 3),
                10, ratings.get(1),
                new HashSet<>(Set.of(genres.get(1), genres.get(2), genres.get(3))));
        filmWithoutGenre = new Film(2, "without_genre", "without_genre_description",
                LocalDate.of(2001, 6, 4),
                20, ratings.get(2),
                new HashSet<>());
        filmWithoutMpa = new Film(1, "without_rating", "without_rating_description",
                LocalDate.of(2003, 7, 8),
                30, null,
                new HashSet<>(Set.of(genres.get(3), genres.get(4), genres.get(5))));
        filmWithoutAll = new Film(1, "without", "without_description",
                LocalDate.of(2005, 10, 11),
                40, null,
                new HashSet<>());
        user = new User(10, "user@mail.com", "user_login", "user_name",
                LocalDate.of(2000, 5, 3), new HashSet<>());
    }

    @Test
    public void createFilmSuccess() {
        var returnedFilm = filmDbStorage.create(film);
        film.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);

        returnedFilm = filmDbStorage.create(filmWithoutGenre);
        filmWithoutGenre.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutGenre);

        returnedFilm = filmDbStorage.create(filmWithoutMpa);
        filmWithoutMpa.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutMpa);

        returnedFilm = filmDbStorage.create(filmWithoutAll);
        filmWithoutAll.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutAll);
    }

    @Test
    public void createFilmBadMpa() {
        filmWithoutMpa.setMpa(new RatingMpa(9999999, ""));
        assertThatThrownBy(() -> filmDbStorage.create(filmWithoutMpa))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void createFilmBadGenre() {
        film.addGenre(new Genre(9999999, ""));
        assertThatThrownBy(() -> filmDbStorage.create(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmSuccess() {
        var id = filmDbStorage.create(film).getId();
        filmWithoutMpa.setId(id);
        var returnedFilm = filmDbStorage.update(filmWithoutMpa);
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutMpa);
    }

    @Test
    public void updateFilmBadMpa() {
        var id = filmDbStorage.create(film).getId();
        filmWithoutMpa.setId(id);
        filmWithoutMpa.setMpa(new RatingMpa(9999999, ""));
        assertThatThrownBy(() -> filmDbStorage.update(filmWithoutMpa))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmBadGenre() {
        var id = filmDbStorage.create(film).getId();
        film.setId(id);
        film.addGenre(new Genre(9999999, ""));
        assertThatThrownBy(() -> filmDbStorage.update(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void findAllEmpty() {
        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void findAllNotEmpty() {
        filmDbStorage.create(film);
        filmDbStorage.create(filmWithoutMpa);
        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void findByIdFilmSuccess() {
        var id = filmDbStorage.create(film).getId();
        film.setId(id);
        assertThat(filmDbStorage.findById(id))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void findByIdFilmNotFound() {
        assertThatThrownBy(() -> filmDbStorage.findById(10L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    public void getPopularEmpty() {
        assertThat(filmDbStorage.getPopular(10))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getPopularWithoutLikes() {
        filmDbStorage.create(film);
        filmDbStorage.create(filmWithoutMpa);
        assertThat(filmDbStorage.getPopular(10))
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void getPopularWithLikes() {
        List<Long> filmsId = new ArrayList<>();
        filmsId.add(filmDbStorage.create(film).getId());
        filmsId.add(filmDbStorage.create(filmWithoutMpa).getId());
        filmsId.add(filmDbStorage.create(filmWithoutGenre).getId());
        filmsId.add(filmDbStorage.create(filmWithoutAll).getId());

        List<Long> usersId = new ArrayList<>();
        usersId.add(userDbStorage.create(user).getId());
        usersId.add(userDbStorage.create(user).getId());
        usersId.add(userDbStorage.create(user).getId());

        filmDbStorage.putLike(filmsId.get(2), usersId.get(0));
        filmDbStorage.putLike(filmsId.get(2), usersId.get(1));
        filmDbStorage.putLike(filmsId.get(2), usersId.get(2));

        filmDbStorage.putLike(filmsId.get(0), usersId.get(0));
        filmDbStorage.putLike(filmsId.get(0), usersId.get(1));

        filmDbStorage.putLike(filmsId.get(3), usersId.get(0));

        assertThat(filmDbStorage.getPopular(2))
                .isNotNull()
                .hasSize(2);
        var films = filmDbStorage.getPopular(10);
        assertThat(films)
                .isNotNull()
                .hasSize(filmsId.size());
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(2));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(0));
        assertThat(films.get(2).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(3));
        assertThat(films.get(3).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(1));
    }

    @Test
    public void putLike() {
        assertThatThrownBy(() -> filmDbStorage.findById(10L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}