package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("ratingMpaDbStorage")
@RequiredArgsConstructor
public class DbMpaStorage implements MpaStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM rating;", this::makeAllMpa);
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query("SELECT * FROM rating WHERE id = :id", namedParameters, this::makeMpa);
    }

    private Optional<Mpa> makeMpa(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return Optional.of(new Mpa(resultSet.getInt("id"), resultSet.getString("name")));
        }
        return Optional.empty();
    }

    private Mpa makeAllMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"), resultSet.getString("name"));
    }
}