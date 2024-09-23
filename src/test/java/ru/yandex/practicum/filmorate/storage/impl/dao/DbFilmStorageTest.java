package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFilmStorageTest {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private User user;
    private Film film;
    private Film filmWithoutGenre;
    private Film filmWithoutMpa;
    private Film filmWithoutAll;
    private Film filmWithAll;
    private DbFilmStorage dbFilmStorage;
    private DbUserStorage dbUserStorage;

    private static final Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Комедия"),
            2, new Genre(2, "Драма"),
            3, new Genre(3, "Мультфильм"),
            4, new Genre(4, "Триллер"),
            5, new Genre(5, "Документальный"),
            6, new Genre(6, "Боевик")
    );

    private static final Map<Integer, Mpa> ratings = Map.of(
            1, new Mpa(1, "G"),
            2, new Mpa(2, "PG"),
            3, new Mpa(3, "PG-13"),
            4, new Mpa(4, "R"),
            5, new Mpa(5, "NC-17")
    );

    @BeforeEach
    public void init() {
        dbFilmStorage = new DbFilmStorage(jdbcTemplate);
        dbUserStorage = new DbUserStorage(jdbcTemplate);

        film = new Film(1, "film_name", "film_description",
                LocalDate.of(2000, 5, 3),
                10, ratings.get(1),
                new LinkedHashSet<>(Set.of(genres.get(1), genres.get(2), genres.get(3))),
                new LinkedHashSet<>()
        );
        filmWithoutGenre = new Film(2, "without_genre", "without_genre_description",
                LocalDate.of(2003, 6, 4),
                20, ratings.get(2),
                new LinkedHashSet<>(),
                new LinkedHashSet<>()
        );
        filmWithoutMpa = new Film(1, "without_rating", "without_rating_description",
                LocalDate.of(2003, 7, 8),
                30, null,
                new LinkedHashSet<>(Set.of(genres.get(3), genres.get(4), genres.get(5))),
                new LinkedHashSet<>()
        );
        filmWithoutAll = new Film(1, "without", "without_description",
                LocalDate.of(2005, 10, 11),
                40, null,
                new LinkedHashSet<>(),
                new LinkedHashSet<>()
        );
        filmWithAll = new Film(1, "all name", "all description",
                LocalDate.of(2000, 10, 5),
                10, ratings.get(2),
                new LinkedHashSet<>(Set.of(genres.get(2), genres.get(5))),
                new LinkedHashSet<>()
        );
        user = new User(10, "user@mail.com", "user_login", "user_name",
                LocalDate.of(2000, 5, 3), new HashSet<>());
    }

    @Test
    public void createFilmSuccess() {
        var returnedFilm = dbFilmStorage.create(film);
        film.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);

        returnedFilm = dbFilmStorage.create(filmWithoutGenre);
        filmWithoutGenre.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutGenre);

        returnedFilm = dbFilmStorage.create(filmWithoutMpa);
        filmWithoutMpa.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutMpa);

        returnedFilm = dbFilmStorage.create(filmWithoutAll);
        filmWithoutAll.setId(returnedFilm.getId());
        assertThat(returnedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutAll);
    }

    @Test
    public void createFilmBadMpa() {
        filmWithoutMpa.setMpa(new Mpa(9999999, ""));
        assertThatThrownBy(() -> dbFilmStorage.create(filmWithoutMpa))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void createFilmBadGenre() {
        film.addGenre(new Genre(9999999, ""));
        assertThatThrownBy(() -> dbFilmStorage.create(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmSuccess() {
        var id = dbFilmStorage.create(film).getId();
        filmWithoutMpa.setId(id);
        assertThat(dbFilmStorage.update(filmWithoutMpa).get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithoutMpa);
    }

    @Test
    public void updateFilmBadMpa() {
        var id = dbFilmStorage.create(film).getId();
        filmWithoutMpa.setId(id);
        filmWithoutMpa.setMpa(new Mpa(9999999, ""));
        assertThatThrownBy(() -> dbFilmStorage.update(filmWithoutMpa))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateFilmBadGenre() {
        var id = dbFilmStorage.create(film).getId();
        film.setId(id);
        film.addGenre(new Genre(9999999, ""));
        assertThatThrownBy(() -> dbFilmStorage.update(film))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void findAllEmpty() {
        assertThat(dbFilmStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void findAllNotEmpty() {
        dbFilmStorage.create(film);
        dbFilmStorage.create(filmWithoutMpa);
        assertThat(dbFilmStorage.findAll())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void findByIdFilmSuccess() {
        var id = dbFilmStorage.create(film).getId();
        film.setId(id);
        assertThat(dbFilmStorage.findById(id).get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void findByIdFilmNotFound() {
        assertThat(dbFilmStorage.findById(10L)).isEmpty();
    }

    @Test
    public void getPopularEmpty() {
        assertThat(dbFilmStorage.getPopular(10, null, null))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getPopularWithoutLikes() {
        dbFilmStorage.create(film);
        dbFilmStorage.create(filmWithoutMpa);
        assertThat(dbFilmStorage.getPopular(10, null, null))
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void getPopularWithLikes() {
        var filmsId = initDb();

        assertThat(dbFilmStorage.getPopular(2, null, null))
                .isNotNull()
                .hasSize(2);
        var films = dbFilmStorage.getPopular(10, null, null);
        assertThat(films)
                .isNotNull()
                .hasSize(filmsId.size());
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(4));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(2));
        assertThat(films.get(2).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(0));
        assertThat(films.get(3).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(3));
        assertThat(films.get(4).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(1));
    }

    @Test
    public void getPopularWithYear() {
        var filmsId = initDb();

        assertThat(dbFilmStorage.getPopular(1, null, filmWithoutMpa.getReleaseDate().getYear()))
                .isNotNull()
                .hasSize(1);
        var films = dbFilmStorage.getPopular(10, null, filmWithoutMpa.getReleaseDate().getYear());
        assertThat(films)
                .isNotNull()
                .hasSize(2);
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(2));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(1));
        films = dbFilmStorage.getPopular(10, null, filmWithoutAll.getReleaseDate().getYear());
        assertThat(films)
                .isNotNull()
                .hasSize(1);
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(3));
    }

    @Test
    public void getPopularWithGenre() {
        var filmsId = initDb();

        assertThat(dbFilmStorage.getPopular(1, 3, null))
                .isNotNull()
                .hasSize(1);
        var films = dbFilmStorage.getPopular(10, 3, null);
        assertThat(films)
                .isNotNull()
                .hasSize(2);
        System.out.println();
        films.forEach(System.out::println);
        System.out.println();
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(0));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(1));
    }

    @Test
    public void getPopularWithGenreAndYear() {
        var filmsId = initDb();

        assertThat(dbFilmStorage.getPopular(1, 1, film.getReleaseDate().getYear()))
                .isNotNull()
                .hasSize(1);
        assertThat(dbFilmStorage.getPopular(10, 1, film.getReleaseDate().getYear()))
                .isNotNull()
                .hasSize(1);
        assertThat(dbFilmStorage.getPopular(10, 3, filmWithoutMpa.getReleaseDate().getYear()))
                .isNotNull()
                .hasSize(1);
        assertThat(dbFilmStorage.getPopular(1, 2, film.getReleaseDate().getYear()))
                .isNotNull()
                .hasSize(1);
        var films = dbFilmStorage.getPopular(10, 2, film.getReleaseDate().getYear());
        assertThat(films)
                .isNotNull()
                .hasSize(2);
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(4));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(0));
    }

    private List<Long> initDb() {
        List<Long> filmsId = new ArrayList<>();
        filmsId.add(dbFilmStorage.create(film).getId());
        filmsId.add(dbFilmStorage.create(filmWithoutMpa).getId());
        filmsId.add(dbFilmStorage.create(filmWithoutGenre).getId());
        filmsId.add(dbFilmStorage.create(filmWithoutAll).getId());
        filmsId.add(dbFilmStorage.create(filmWithAll).getId());

        List<Long> usersId = new ArrayList<>();
        usersId.add(dbUserStorage.create(user).getId());
        usersId.add(dbUserStorage.create(user).getId());
        usersId.add(dbUserStorage.create(user).getId());
        usersId.add(dbUserStorage.create(user).getId());

        // most popular filmWithoutGenre id == 5, likes amount == 4
        dbFilmStorage.putLike(filmsId.get(4), usersId.get(0));
        dbFilmStorage.putLike(filmsId.get(4), usersId.get(1));
        dbFilmStorage.putLike(filmsId.get(4), usersId.get(2));
        dbFilmStorage.putLike(filmsId.get(4), usersId.get(3));

        // filmWithoutGenre id == 3, likes amount == 3
        dbFilmStorage.putLike(filmsId.get(2), usersId.get(0));
        dbFilmStorage.putLike(filmsId.get(2), usersId.get(1));
        dbFilmStorage.putLike(filmsId.get(2), usersId.get(2));

        // film id == 1, likes amount == 2
        dbFilmStorage.putLike(filmsId.get(0), usersId.get(0));
        dbFilmStorage.putLike(filmsId.get(0), usersId.get(1));

        // filmWithoutAll id == 4, likes amount == 1
        dbFilmStorage.putLike(filmsId.get(3), usersId.get(0));

        // filmWithoutMpa id == 2, likes amount == 0
        return filmsId;
    }

    @Test
    public void getCommonEmpty() {
        assertThat(dbFilmStorage.getCommon(1, 2))
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getCommonNotEmpty() {
        var filmsId = initDb();

        var films = dbFilmStorage.getCommon(1, 2);
        assertThat(films)
                .isNotNull()
                .hasSize(3);
        assertThat(films.get(0).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(4));
        assertThat(films.get(1).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(2));
        assertThat(films.get(2).getId())
                .isNotNull()
                .isEqualTo(filmsId.get(0));
    }
}