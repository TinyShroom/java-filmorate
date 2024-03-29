package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    @Transactional
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO film(name, description, release_date, duration, rating_id) " +
                "VALUES (:name, :description, :release_date, :duration, :rating_id);";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rating_id", film.getMpa() == null ? null : film.getMpa().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        updateFilmGenre(film);
        return film;
    }

    @Override
    @Transactional
    public Optional<Film> update(Film film) {
        String sqlUpdateQuery = "UPDATE film " +
                        "SET " +
                        "name = :name, " +
                        "description = :description, " +
                        "release_date = :release_date, " +
                        "duration = :duration, " +
                        "rating_id = :rating_id " +
                        "WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rating_id", film.getMpa() == null ? null : film.getMpa().getId())
                .addValue("id", film.getId());

        var value = jdbcTemplate.update(sqlUpdateQuery, namedParameters);
        if (value < 1) {
            return Optional.empty();
        }
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = :id", namedParameters);
        updateFilmGenre(film);
        return Optional.of(film);
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
        var films = jdbcTemplate.query(sqlReadFilmQuery, this::makeFilms);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id;";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        addGenreInFilms(films, filmGenres);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "WHERE f.id = :id;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        var film = jdbcTemplate.query(sqlReadFilmQuery, namedParameters, this::makeFilm);
        if (film == null) {
            return Optional.empty();
        }
        String sqlReadGenreQuery = "SELECT g.id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "WHERE f.film_id = :id;";
        var genres = jdbcTemplate.query(sqlReadGenreQuery, namedParameters, this::makeGenre);
        for (var genre: genres) {
            film.addGenre(genre);
        }
        return Optional.of(film);
    }

    @Override
    public void putLike(Long filmId, Long userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId);
        jdbcTemplate.update("MERGE INTO film_likes(film_id, user_id) values (:film_id, :user_id)", namedParameters);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("user_id", userId);
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = :film_id AND user_id = :user_id", namedParameters);
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
                "LIMIT :count;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("count", count);
        var films = jdbcTemplate.query(sqlReadFilmQuery, namedParameters, this::makeFilms);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "ORDER BY f.film_id;";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        addGenreInFilms(films, filmGenres);
        return films;
    }

    private void addGenreInFilms(List<Film> films, List<FilmGenre> filmGenres) {
        Map<Long, Set<Genre>> genresMap = new HashMap<>();
        for (var filmGenre: filmGenres) {
            genresMap.putIfAbsent(filmGenre.getId(), new LinkedHashSet<>());
            genresMap.get(filmGenre.getId()).add(filmGenre.getGenre());
        }
        for (var film: films) {
            var genres = genresMap.getOrDefault(film.getId(), new LinkedHashSet<>());
            for (var genre: genres) {
                film.addGenre(genre);
            }
        }
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

    private Film makeFilms(ResultSet resultSet, int rowNum) throws SQLException {
        var releaseDate = resultSet.getDate("release_date");
        var releaseLocalDate = releaseDate == null ? null : releaseDate.toLocalDate();
        var rating = resultSet.getInt("rating_id") < 1 ? null :
                new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name"));
        return new Film(resultSet.getLong("id"),
                resultSet.getString("film_name"),
                resultSet.getString("description"),
                releaseLocalDate,
                resultSet.getInt("duration"),
                rating,
                new LinkedHashSet<>()
        );
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            var releaseDate = resultSet.getDate("release_date");
            var releaseLocalDate = releaseDate == null ? null : releaseDate.toLocalDate();
            var rating = resultSet.getInt("rating_id") < 1 ? null :
                    new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name"));
            return new Film(resultSet.getLong("id"),
                    resultSet.getString("film_name"),
                    resultSet.getString("description"),
                    releaseLocalDate,
                    resultSet.getInt("duration"),
                    rating,
                    new LinkedHashSet<>()
            );
        }
        return null;
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

    private void updateFilmGenre(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            var batch = SqlParameterSourceUtils.createBatch(film.getGenres().stream()
                    .map(t -> (Map.of("film_id", film.getId(), "genre_id", t.getId())))
                    .collect(Collectors.toList()));
            jdbcTemplate.batchUpdate("INSERT INTO film_genre(film_id, genre_id) VALUES (:film_id, :genre_id)", batch);
        }
    }

    @Data
    @AllArgsConstructor
    private static class FilmGenre {
        private long id;
        private Genre genre;
    }
}
