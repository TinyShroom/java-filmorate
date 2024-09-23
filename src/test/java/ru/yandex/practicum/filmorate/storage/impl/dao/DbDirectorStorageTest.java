package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.yandex.practicum.filmorate.model.Director;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbDirectorStorageTest {

    private final NamedParameterJdbcOperations jdbcOperations;
    private Director director;
    private Director secondDirector;
    private DbDirectorStorage dbDirectorStorage;

    @BeforeEach
    public void init() {
        dbDirectorStorage = new DbDirectorStorage(jdbcOperations);
        director = new Director(1, "dir name");
        secondDirector = new Director(2, "second name");
    }

    @Test
    public void createSuccess() {
        var returnedDirector = dbDirectorStorage.create(director);
        director.setId(returnedDirector.getId());
        assertThat(returnedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    public void createNullNameFail() {
        assertThatThrownBy(() -> dbDirectorStorage.create(new Director(1, null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void updateNotFound() {
        assertThat(dbDirectorStorage.update(director)).isEmpty();
    }

    @Test
    public void updateSuccess() {
        var id = dbDirectorStorage.create(director).getId();
        var newDirector = new Director(id, "new name");
        var returnedDirector = dbDirectorStorage.update(newDirector);
        assertThat(returnedDirector.get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newDirector);
    }

    @Test
    public void findAllEmpty() {
        assertThat(dbDirectorStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void findAllNotEmpty() {
        dbDirectorStorage.create(director);
        dbDirectorStorage.create(secondDirector);
        assertThat(dbDirectorStorage.findAll())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void findByIdNotFound() {
        assertThat(dbDirectorStorage.findById(10L)).isEmpty();
    }

    @Test
    public void findByIdSuccess() {
        var id = dbDirectorStorage.create(director).getId();
        director.setId(id);
        assertThat(dbDirectorStorage.findById(id).get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    public void deleteSuccess() {
        var id = dbDirectorStorage.create(director).getId();
        dbDirectorStorage.delete(id);
        assertThat(dbDirectorStorage.findById(id))
                .isEmpty();
    }
}