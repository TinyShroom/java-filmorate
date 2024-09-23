package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class DbDirectorStorage implements DirectorStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO director(name) VALUES (:name);";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", director.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Optional<Director> update(Director director) {
        String sqlUpdateQuery =
                "UPDATE director " +
                        "SET " +
                        "name = :name " +
                        "WHERE id = :id;";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", director.getName())
                .addValue("id", director.getId());

        var value = jdbcTemplate.update(sqlUpdateQuery, namedParameters);
        if (value < 1) {
            return Optional.empty();
        }
        return Optional.of(director);
    }

    @Override
    public List<Director> findAll() {
        return jdbcTemplate.query("SELECT * FROM director;", this::makeDirectors);
    }

    @Override
    public Optional<Director> findById(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query("SELECT * FROM director WHERE id = :id;", namedParameters, this::makeDirector);
    }

    @Override
    public void delete(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update("DELETE FROM director WHERE id = :id;", namedParameters);
    }

    private Optional<Director> makeDirector(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(new Director(resultSet.getLong("id"), resultSet.getString("name")));
        }
        return Optional.empty();
    }

    private Director makeDirectors(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(resultSet.getLong("id"), resultSet.getString("name"));
    }
}
