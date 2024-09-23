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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
        updateFilmDirector(film);
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
        jdbcTemplate.update("DELETE FROM film_director WHERE film_id = :id", namedParameters);
        updateFilmDirector(film);
        return Optional.of(film);
    }

    @Override
    public List<Film> findAll() {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "       f.name AS film_name,\n" +
                "       f.description,\n" +
                "       f.release_date,\n" +
                "       f.duration,\n" +
                "       f.rating_id,\n" +
                "       r.name AS rating_name,\n" +
                "       ARRAY_AGG(fg.genre_id) AS film_genres_id,\n" +
                "       ARRAY_AGG(g.name) AS film_genres_name,\n" +
                "       ARRAY_AGG(fd.director_id) AS film_director_id,\n" +
                "       ARRAY_AGG(d.name) AS film_director_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.id\n" +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id\n" +
                "LEFT JOIN director AS d ON fd.director_id = d.id\n" +
                "GROUP BY f.id;";
        return jdbcTemplate.query(sqlReadFilmQuery, this::makeAllFilms);
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sqlReadFilmQuery = "SELECT f.id,\n" +
                "       f.name AS film_name,\n" +
                "       f.description,\n" +
                "       f.release_date,\n" +
                "       f.duration,\n" +
                "       f.rating_id,\n" +
                "       r.name AS rating_name,\n" +
                "       ARRAY_AGG(fg.genre_id) AS film_genres_id,\n" +
                "       ARRAY_AGG(g.name) AS film_genres_name,\n" +
                "       ARRAY_AGG(fd.director_id) AS film_director_id,\n" +
                "       ARRAY_AGG(d.name) AS film_director_name\n" +
                "FROM film AS f\n" +
                "LEFT JOIN rating AS r ON f.rating_id = r.id\n" +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.id\n" +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id\n" +
                "LEFT JOIN director AS d ON fd.director_id = d.id\n" +
                "WHERE f.id = :id\n" +
                "GROUP BY f.id;";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query(sqlReadFilmQuery, namedParameters, this::makeFilm);
    }

    @Override
    public void delete(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        jdbcTemplate.update("DELETE FROM film WHERE id = :id", namedParameters);
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
    public List<Film> getPopular(int count, Integer genreId, Integer year) {
        var where = getWhere(genreId, year);
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
                where +
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
        String sqlReadDirectorQuery = "SELECT f.film_id,\n" +
                "    f.director_id,\n" +
                "    d.name\n" +
                "FROM director AS d\n" +
                "JOIN film_director AS f ON f.director_id = d.id\n" +
                "ORDER BY f.film_id;";
        var filmDirectors = jdbcTemplate.query(sqlReadDirectorQuery, this::makeFilmDirector);
        addDirectorInFilms(films, filmDirectors);
        return films;
    }

    @Override
    public List<Film> getByDirectorId(Long id, String sortBy) {
        String sqlReadFilmQuery = getDirectorIdQuery(sortBy);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        var films = jdbcTemplate.query(sqlReadFilmQuery, namedParameters, this::makeFilms);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "ORDER BY f.film_id;";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        addGenreInFilms(films, filmGenres);
        String sqlReadDirectorQuery = "SELECT f.film_id,\n" +
                "    f.director_id,\n" +
                "    d.name\n" +
                "FROM director AS d\n" +
                "JOIN film_director AS f ON f.director_id = d.id\n" +
                "ORDER BY f.film_id;";
        var filmDirectors = jdbcTemplate.query(sqlReadDirectorQuery, this::makeFilmDirector);
        addDirectorInFilms(films, filmDirectors);
        return films;
    }

    @Override
    public List<Film> getFilmRecommendation(Long userId, Long userWithSimilarLikesId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("user_id", userId)
                .addValue("another_user_id", userWithSimilarLikesId);
        String sqlQuery = "SELECT film.id," +
                "       film.name AS film_name," +
                "       film.description," +
                "       film.release_date," +
                "       film.duration," +
                "       film.rating_id," +
                "       r.name AS rating_name " +
                "FROM film " +
                "LEFT JOIN rating AS r ON film.rating_id = r.id " +
                "WHERE film.ID IN (SELECT FILM_ID" +
                "             FROM FILM_LIKES" +
                "             WHERE USER_ID = :another_user_id)" +
                "  AND film.ID NOT IN (SELECT FILM_ID" +
                "                 FROM FILM_LIKES" +
                "                 WHERE USER_ID = :user_id) " +
                "GROUP BY FILM.ID;";
        var films = jdbcTemplate.query(sqlQuery, namedParameters, this::makeFilms);
        String sqlReadGenreQuery = "SELECT f.film_id,\n" +
                "    f.genre_id,\n" +
                "    g.name\n" +
                "FROM genre AS g\n" +
                "JOIN film_genre AS f ON f.genre_id = g.id\n" +
                "ORDER BY f.film_id;";
        String sqlReadDirectorQuery = "SELECT f.film_id,\n" +
                "    f.director_id,\n" +
                "    d.name\n" +
                "FROM director AS d\n" +
                "JOIN film_director AS f ON f.director_id = d.id\n" +
                "ORDER BY f.film_id;";
        var filmDirectors = jdbcTemplate.query(sqlReadDirectorQuery, this::makeFilmDirector);
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        addGenreInFilms(films, filmGenres);
        addDirectorInFilms(films, filmDirectors);

        return films;
    }

    @Override
    public List<Film> findByTitle(String query) {
        String sqlQuery = "SELECT f.id, " +
                "       f.name AS film_name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.rating_id, " +
                "       r.name AS rating_name, " +
                "       GROUP_CONCAT(DISTINCT fg.genre_id) AS film_genres_id, " +
                "       GROUP_CONCAT(DISTINCT g.name) AS film_genres_name, " +
                "       GROUP_CONCAT(DISTINCT fd.director_id) AS film_director_id, " +
                "       GROUP_CONCAT(DISTINCT d.name) AS film_director_name " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN director AS d ON fd.director_id = d.id " +
                "WHERE LOWER(f.name) LIKE LOWER(:query) " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("query", "%" + query + "%");
        return jdbcTemplate.query(sqlQuery, namedParameters, this::makeAllFilms);
    }

    @Override
    public List<Film> findByDirectorName(String query) {
        String sqlQuery = "SELECT f.id, " +
                "       f.name AS film_name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.rating_id, " +
                "       r.name AS rating_name " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.id " +
                "WHERE f.id IN (SELECT fd.film_id FROM film_director AS fd " +
                "               INNER JOIN director AS d ON fd.director_id = d.id " +
                "               WHERE LOWER(d.name) LIKE LOWER(:query))";
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("query", "%" + query + "%");
        var films = jdbcTemplate.query(sqlQuery, namedParameters, this::makeFilms);

        String sqlReadGenreQuery = "SELECT f.film_id, " +
                "    f.genre_id, " +
                "    g.name " +
                "FROM genre AS g " +
                "JOIN film_genre AS f ON f.genre_id = g.id " +
                "ORDER BY f.film_id";
        var filmGenres = jdbcTemplate.query(sqlReadGenreQuery, this::makeFilmGenre);
        addGenreInFilms(films, filmGenres);

        String sqlReadDirectorQuery = "SELECT f.film_id, " +
                "    f.director_id, " +
                "    d.name " +
                "FROM director AS d " +
                "JOIN film_director AS f ON f.director_id = d.id " +
                "ORDER BY f.film_id";
        var filmDirectors = jdbcTemplate.query(sqlReadDirectorQuery, this::makeFilmDirector);
        addDirectorInFilms(films, filmDirectors);

        return films;
    }

    @Override
    public List<Film> findByTitleOrDirectorName(String titleQuery, String directorQuery) {
        String sqlQuery = "SELECT DISTINCT f.id, " +
                "       f.name AS film_name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.rating_id, " +
                "       r.name AS rating_name, " +
                "       fg.genre_id AS film_genres_id, " +
                "       g.name AS film_genres_name, " +
                "       fd.director_id AS film_director_id, " +
                "       d.name AS film_director_name, " +
                "       COUNT(fl.film_id) AS like_count " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN director AS d ON fd.director_id = d.id " +
                "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                "WHERE LOWER(f.name) LIKE LOWER(:titleQuery) OR LOWER(d.name) LIKE LOWER(:directorQuery) " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name, " +
                "         fg.genre_id, g.name, fd.director_id, d.name " +
                "ORDER BY like_count DESC";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("titleQuery", "%" + titleQuery + "%")
                .addValue("directorQuery", "%" + directorQuery + "%");

        return jdbcTemplate.query(sqlQuery, namedParameters, this::makeAllFilms);
    }

    private static String getWhere(Integer genreId, Integer year) {
        if (genreId == null && year == null) return "";
        var dateQuery = String.format("f.release_date BETWEEN '%1$d-01-01' AND '%1$d-12-31'", year);
        var genreQuery = String.format("f.id IN (SELECT film_id FROM film_genre WHERE genre_id = %d)", genreId);
        if (genreId != null && year != null) {
            return "WHERE (" + dateQuery + ") AND (" + genreQuery + ")\n";
        }
        if (genreId != null) {
            return "WHERE " + genreQuery + "\n";
        }
        return "WHERE " + dateQuery + "\n";
    }

    private static String getDirectorIdQuery(String sortBy) {
        String orderBy;
        if ("year".equals(sortBy)) {
            orderBy = "ORDER BY f.release_date";
        } else {
            orderBy = "ORDER BY COUNT(l.film_id) DESC";
        }
        return "SELECT f.id, " +
                "    f.name AS film_name, " +
                "    f.description, " +
                "    f.release_date, " +
                "    f.duration, " +
                "    f.rating_id, " +
                "    r.name AS rating_name " +
                "FROM film AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.id " +
                "LEFT JOIN film_likes AS l ON f.id = l.film_id " +
                "WHERE f.id IN ( " +
                "   SELECT film_id " +
                "   FROM film_director " +
                "   WHERE director_id = :id " +
                ") " +
                "GROUP BY f.id " +
                orderBy + ";";
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        String sqlReadFilmQuery = "WITH common_ids AS (\n" +
                "    SELECT film_id\n" +
                "    FROM film_likes\n" +
                "    WHERE user_id = :userId AND film_id IN (\n" +
                "        SELECT film_id\n" +
                "        FROM film_likes\n" +
                "        WHERE user_id = :friendId\n" +
                "    )\n" +
                ")\n" +
                "SELECT f.id,\n" +
                "    f.name AS film_name,\n" +
                "    f.description,\n" +
                "    f.release_date,\n" +
                "    f.duration,\n" +
                "    f.rating_id,\n" +
                "    r.name AS rating_name,\n" +
                "    (SELECT ARRAY_AGG(genre_id)\n" +
                "     FROM film_genre\n" +
                "     WHERE film_id = f.id) AS film_genres_id,\n" +
                "     \n" +
                "    (SELECT ARRAY_AGG(name)\n" +
                "     FROM genre\n" +
                "     WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = f.id)) AS film_genres_name,\n" +
                "     \n" +
                "    (SELECT ARRAY_AGG(director_id)\n" +
                "     FROM film_director\n" +
                "     WHERE film_id = f.id) AS film_director_id,\n" +
                "     \n" +
                "    (SELECT ARRAY_AGG(name)\n" +
                "     FROM director\n" +
                "     WHERE id IN (SELECT director_id FROM film_director WHERE film_id = f.id)) AS film_director_name\n" +
                "\n" +
                "FROM film f INNER JOIN common_ids ON f.id = common_ids.film_id\n" +
                "INNER JOIN film_likes fl ON f.id = fl.film_id\n" +
                "INNER JOIN rating r ON f.rating_id = r.id\n" +
                "GROUP BY f.id\n" +
                "ORDER BY count(fl.film_id) DESC;";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);

        return jdbcTemplate.query(sqlReadFilmQuery, namedParameters, this::makeAllFilms);
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

    private void addDirectorInFilms(List<Film> films, List<FilmDirector> filmDirectors) {
        Map<Long, Set<Director>> genresMap = new HashMap<>();
        for (var filmDirector: filmDirectors) {
            genresMap.putIfAbsent(filmDirector.getId(), new LinkedHashSet<>());
            genresMap.get(filmDirector.getId()).add(filmDirector.getDirector());
        }
        for (var film: films) {
            var directors = genresMap.getOrDefault(film.getId(), new LinkedHashSet<>());
            for (var director: directors) {
                film.addDirector(director);
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
                new LinkedHashSet<>(),
                new LinkedHashSet<>()
        );
    }

    private Film makeAllFilms(ResultSet resultSet, int rowNum) throws SQLException {
        var releaseDate = resultSet.getDate("release_date");
        var releaseLocalDate = releaseDate == null ? null : releaseDate.toLocalDate();
        var rating = resultSet.getInt("rating_id") < 1 ? null :
                new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name"));
        var film = new Film(resultSet.getLong("id"),
                resultSet.getString("film_name"),
                resultSet.getString("description"),
                releaseLocalDate,
                resultSet.getInt("duration"),
                rating,
                new LinkedHashSet<>(),
                new LinkedHashSet<>()
        );
        var filmGenresId = resultSet.getArray("film_genres_id");
        var filmGenresName = resultSet.getArray("film_genres_name");
        if (filmGenresId != null &&  filmGenresName != null) {
            var genresId = Arrays.stream((Object[]) filmGenresId.getArray())
                    .filter(Objects::nonNull)
                    .mapToInt((t) -> (Integer) t)
                    .toArray();
            var genresName = Arrays.stream((Object[]) filmGenresName.getArray())
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toArray(String[]::new);
            for (var i = 0; i < genresId.length && i < genresName.length; ++i) {
                film.addGenre(new Genre(genresId[i], genresName[i]));
            }
        }
        var filmDirectorId = resultSet.getArray("film_director_id");
        var filmDirectorName = resultSet.getArray("film_director_name");
        if (filmDirectorId != null &&  filmDirectorName != null) {
            var directorId = Arrays.stream((Object[]) filmDirectorId.getArray())
                    .filter(Objects::nonNull)
                    .mapToLong((t) -> (Long) t)
                    .toArray();
            var directorName = Arrays.stream((Object[]) filmDirectorName.getArray())
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toArray(String[]::new);
            for (var i = 0; i < directorId.length && i < directorName.length; ++i) {
                film.addDirector(new Director(directorId[i], directorName[i]));
            }
        }
        return film;
    }

    private Optional<Film> makeFilm(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            var releaseDate = resultSet.getDate("release_date");
            var releaseLocalDate = releaseDate == null ? null : releaseDate.toLocalDate();
            var rating = resultSet.getInt("rating_id") < 1 ? null :
                    new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name"));
            var film = new Film(resultSet.getLong("id"),
                    resultSet.getString("film_name"),
                    resultSet.getString("description"),
                    releaseLocalDate,
                    resultSet.getInt("duration"),
                    rating,
                    new LinkedHashSet<>(),
                    new LinkedHashSet<>()
            );

            var filmGenresId = resultSet.getArray("film_genres_id");
            var filmGenresName = resultSet.getArray("film_genres_name");
            if (filmGenresId != null &&  filmGenresName != null) {
                var genresId = Arrays.stream((Object[]) filmGenresId.getArray())
                        .filter(Objects::nonNull)
                        .mapToInt((t) -> (Integer) t)
                        .toArray();
                var genresName = Arrays.stream((Object[]) filmGenresName.getArray())
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .toArray(String[]::new);
                for (var i = 0; i < genresId.length && i < genresName.length; ++i) {
                    film.addGenre(new Genre(genresId[i], genresName[i]));
                }
            }
            var filmDirectorId = resultSet.getArray("film_director_id");
            var filmDirectorName = resultSet.getArray("film_director_name");
            if (filmDirectorId != null &&  filmDirectorName != null) {
                var directorId = Arrays.stream((Object[]) filmDirectorId.getArray())
                        .filter(Objects::nonNull)
                        .mapToLong((t) -> (Long) t)
                        .toArray();
                var directorName = Arrays.stream((Object[]) filmDirectorName.getArray())
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .toArray(String[]::new);
                for (var i = 0; i < directorId.length && i < directorName.length; ++i) {
                    film.addDirector(new Director(directorId[i], directorName[i]));
                }
            }
            return Optional.of(film);
        }
        return Optional.empty();
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

    private FilmDirector makeFilmDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new FilmDirector(resultSet.getLong("film_id"),
                new Director(resultSet.getInt("director_id"), resultSet.getString("name"))
        );
    }

    private Map.Entry<Long, Long> makeOrder(ResultSet resultSet, int rowNum) throws SQLException {
        return Map.entry(resultSet.getLong("film_id"), resultSet.getLong("likes"));
    }

    private void updateFilmGenre(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            var batch = SqlParameterSourceUtils.createBatch(film.getGenres().stream()
                    .map(t -> (Map.of("film_id", film.getId(), "genre_id", t.getId())))
                    .collect(Collectors.toList()));
            jdbcTemplate.batchUpdate("INSERT INTO film_genre(film_id, genre_id) VALUES (:film_id, :genre_id)", batch);
        }
    }

    private void updateFilmDirector(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            var batch = SqlParameterSourceUtils.createBatch(film.getDirectors().stream()
                    .map(t -> (Map.of("film_id", film.getId(), "director_id", t.getId())))
                    .collect(Collectors.toList()));
            jdbcTemplate.batchUpdate(
                    "INSERT INTO film_director(film_id, director_id) " +
                            "VALUES (:film_id, :director_id)",
                    batch);
        }
    }

    @Data
    @AllArgsConstructor
    private static class FilmGenre {
        private long id;
        private Genre genre;
    }

    @Data
    @AllArgsConstructor
    private static class FilmDirector {
        private long id;
        private Director director;
    }
}
