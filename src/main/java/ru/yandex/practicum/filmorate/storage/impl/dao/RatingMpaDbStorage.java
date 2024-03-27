package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository("ratingMpaDbStorage")
@RequiredArgsConstructor
public class RatingMpaDbStorage implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<RatingMpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM rating;", this::makeRatingMpa);
    }

    @Override
    public RatingMpa findById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM rating WHERE id = ?", this::makeRatingMpa, id);
    }

    private RatingMpa makeRatingMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new RatingMpa(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
