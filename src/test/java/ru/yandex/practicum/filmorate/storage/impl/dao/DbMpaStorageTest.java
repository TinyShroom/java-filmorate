package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbMpaStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private DbMpaStorage ratingDbMpaStorage;

    private static final Map<Integer, Mpa> ratings = Map.of(
            1, new Mpa(1, "G"),
            2, new Mpa(2, "PG"),
            3, new Mpa(3, "PG-13"),
            4, new Mpa(4, "R"),
            5, new Mpa(5, "NC-17")
    );

    @BeforeEach
    public void init() {
        ratingDbMpaStorage = new DbMpaStorage(jdbcTemplate);
    }

    @Test
    public void findGenresAll() {
        assertThat(ratingDbMpaStorage.findAll())
                .isNotNull()
                .hasSize(ratings.size())
                .usingRecursiveComparison()
                .isEqualTo(ratings.values());
    }

    @Test
    public void findGenresById() {
        var id = 3;
        assertThat(ratingDbMpaStorage.findById(id))
                .isNotNull()
                .isEqualTo(ratings.get(id));
    }

    @Test
    public void findGenresByIdNotFound() {
        var id = Integer.MAX_VALUE;
        assertThatThrownBy(() -> ratingDbMpaStorage.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}