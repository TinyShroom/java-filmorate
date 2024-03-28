package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbGenreStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private DbGenreStorage dbGenreStorage;

    private static final Map<Integer, Genre> genres = Map.of(
            1, new Genre(1, "Комедия"),
            2, new Genre(2, "Драма"),
            3, new Genre(3, "Мультфильм"),
            4, new Genre(4, "Триллер"),
            5, new Genre(5, "Документальный"),
            6, new Genre(6, "Боевик")
    );

    @BeforeEach
    public void init() {
        dbGenreStorage = new DbGenreStorage(jdbcTemplate);
    }

    @Test
    public void findGenresAll() {
        assertThat(dbGenreStorage.findAll())
                .isNotNull()
                .hasSize(genres.size())
                .usingRecursiveComparison()
                .isEqualTo(genres.values());
    }

    @Test
    public void findGenresById() {
        var id = 3;
        assertThat(dbGenreStorage.findById(id))
                .isNotNull()
                .isEqualTo(genres.get(id));
    }

    @Test
    public void findGenresByIdNotFound() {
        var id = Integer.MAX_VALUE;
        assertThatThrownBy(() -> dbGenreStorage.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}