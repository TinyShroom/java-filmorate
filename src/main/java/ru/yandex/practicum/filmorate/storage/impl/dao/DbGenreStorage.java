package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genre;", this::makeGenres);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query("SELECT * FROM genre WHERE id = :id", namedParameters, this::makeGenre);
    }

    private Optional<Genre> makeGenre(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(new Genre(resultSet.getInt("id"), resultSet.getString("name")));
        }
        return Optional.empty();
    }

    private Genre makeGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}