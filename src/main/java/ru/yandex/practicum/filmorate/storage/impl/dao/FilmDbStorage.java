package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ConstraintException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (var genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String sqlUpdateQuery =
                "UPDATE film " +
                        "SET " +
                        "name = ?, " +
                        "description = ?, " +
                        "release_date = ?, " +
                        "duration = ?, " +
                        "rating_id = ? " +
                        "WHERE id = ?";

        var value = jdbcTemplate.update(sqlUpdateQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa() == null ? null : film.getMpa().getId(), film.getId());
        if (value < 1) {
            throw new NotFoundException(String.format("film with id %d not found", film.getId()));
        }
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (var genre: film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }

        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = ?;";

        var updatedFilm = jdbcTemplate.queryForObject(sqlReadFilmQuery, this::makeFilm, film.getId());
        String sqlReadGenreQuery = "SELECT g.id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "WHERE f.film_id = ?;";
        var genres = jdbcTemplate.query(sqlReadGenreQuery, this::makeGenre, updatedFilm.getId());
        for (var genre: genres) {
            updatedFilm.addGenre(genre);
        }
        return updatedFilm;
    }

    @Override
    public List<Film> findAll() {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "ORDER BY f.id;";
        var films = jdbcTemplate.query(sqlReadFilmQuery, this::makeFilm);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "ORDER BY f.film_id;";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        Map<Long, Set<Genre>> genresMap = new HashMap<>();
        for (var filmGenre: filmGenres) {
            genresMap.putIfAbsent(filmGenre.getId(), new HashSet<>());
            genresMap.get(filmGenre.getId()).add(filmGenre.getGenre());
        }
        for (var film: films) {
            var genres = genresMap.getOrDefault(film.getId(), new HashSet<>());
            for (var genre: genres) {
                film.addGenre(genre);
            }
        }
        return films;
    }

    @Override
    public Film findById(Long id) {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = ?;";
        var film = jdbcTemplate.queryForObject(sqlReadFilmQuery, this::makeFilm, id);
        String sqlReadGenreQuery = "SELECT g.id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "WHERE f.film_id = ?\n" +
                "ORDER BY g.id;";
        var genres = jdbcTemplate.query(sqlReadGenreQuery, this::makeGenre, id);
        for (var genre: genres) {
            film.addGenre(genre);
        }
        return film;
    }

    @Override
    public void putLike(Long id, Long userId) {
        try {
            jdbcTemplate.update("INSERT INTO film_likes(film_id, user_id) values (?, ?)", id, userId);
        } catch (Exception e) {
            throw new ConstraintException("film or user not found");
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        var result = jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", id, userId);
        if (result < 1) {
            throw new NotFoundException("film or user not found");
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "LEFT JOIN film_likes AS l ON f.id = l.film_id\n" +
                "GROUP BY f.id\n" +
                "ORDER BY COUNT(l.film_id) DESC\n" +
                "LIMIT ?;";
        var films = jdbcTemplate.query(sqlReadFilmQuery, this::makeFilm, count);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "ORDER BY f.film_id;";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        Map<Long, Set<Genre>> genresMap = new HashMap<>();
        for (var filmGenre: filmGenres) {
            genresMap.putIfAbsent(filmGenre.getId(), new HashSet<>());
            genresMap.get(filmGenre.getId()).add(filmGenre.getGenre());
        }
        for (var film: films) {
            var genres = genresMap.getOrDefault(film.getId(), new HashSet<>());
            for (var genre: genres) {
                film.addGenre(genre);
            }
        }
        return films;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_id", film.getMpa() == null ? null : film.getMpa().getId());
        return values;
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        var releaseDate = resultSet.getDate("release_date");
        var releaseLocalDate = releaseDate == null ? null : releaseDate.toLocalDate();
        var rating = resultSet.getInt("rating_id") < 1 ? null :
                new RatingMpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name"));
        return new Film(resultSet.getLong("id"),
                resultSet.getString("film_name"),
                resultSet.getString("description"),
                releaseLocalDate,
                resultSet.getInt("duration"),
                rating,
                new HashSet<>()
        );
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private FilmGenre makeFilmGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new FilmGenre(resultSet.getLong("film_id"),
                new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"))
        );
    }
}
