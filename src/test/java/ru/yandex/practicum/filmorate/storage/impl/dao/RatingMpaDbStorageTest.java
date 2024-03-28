package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingMpaDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private RatingMpaDbStorage ratingMpaDbStorage;

    private static final Map<Integer, RatingMpa> ratings = Map.of(
            1, new RatingMpa(1, "G"),
            2, new RatingMpa(2, "PG"),
            3, new RatingMpa(3, "PG-13"),
            4, new RatingMpa(4, "R"),
            5, new RatingMpa(5, "NC-17")
    );

    @BeforeEach
    public void init() {
        ratingMpaDbStorage = new RatingMpaDbStorage(jdbcTemplate);
    }

    @Test
    public void findGenresAll() {
        assertThat(ratingMpaDbStorage.findAll())
                .isNotNull()
                .hasSize(ratings.size())
                .usingRecursiveComparison()
                .isEqualTo(ratings.values());
    }

    @Test
    public void findGenresById() {
        var id = 3;
        assertThat(ratingMpaDbStorage.findById(id))
                .isNotNull()
                .isEqualTo(ratings.get(id));
    }

    @Test
    public void findGenresByIdNotFound() {
        var id = Integer.MAX_VALUE;
        assertThatThrownBy(() -> ratingMpaDbStorage.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}