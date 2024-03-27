package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (var genre: film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
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
                film.getDuration(), film.getMpa().getId(), film.getId());
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
                "JOIN rating AS r ON f.rating_id = r.id\n" +
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
    public Collection<Film> findAll() {
        return null;
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
                "JOIN rating AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = ?;";
        var film = jdbcTemplate.queryForObject(sqlReadFilmQuery, this::makeFilm, id);
        String sqlReadGenreQuery = "SELECT g.id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "WHERE f.film_id = ?;";
        var genres = jdbcTemplate.query(sqlReadGenreQuery, this::makeGenre, id);
        for (var genre: genres) {
            film.addGenre(genre);
        }
        return film;
    }

    @Override
    public void putLike(Long id, Long userId) {

    }

    @Override
    public void deleteLike(Long id, Long userId) {

    }

    @Override
    public List<Film> getPopular(int count) {
        return null;
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
        return new Film(resultSet.getLong("id"),
                resultSet.getString("film_name"),
                resultSet.getString("description"),
                releaseLocalDate,
                resultSet.getInt("duration"),
                new RatingMpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name")),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
