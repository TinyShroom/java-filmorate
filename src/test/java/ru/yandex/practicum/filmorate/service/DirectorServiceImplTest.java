package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.impl.DirectorServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbDirectorStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(DirectorService.class)
class DirectorServiceImplTest {

    @MockBean
    private DbDirectorStorage directorStorage;

    private DirectorService directorService;

    @Test
    public void updateNotFound() {
        var director = new Director(-1, "name");
        Mockito.when(directorStorage.update(director))
                .thenReturn(Optional.empty());
        directorService = new DirectorServiceImpl(directorStorage);
        assertThrows(NotFoundException.class, () -> directorService.update(director));
    }

    @Test
    public void findByIdNotFound() {
        var id = -1L;
        Mockito.when(directorStorage.findById(id))
                .thenReturn(Optional.empty());
        directorService = new DirectorServiceImpl(directorStorage);
        assertThrows(NotFoundException.class, () -> directorService.getById(id));
    }

    @Test
    public void deleteNotFound() {
        var id = -1L;
        Mockito.when(directorStorage.findById(id))
                .thenReturn(Optional.empty());
        directorService = new DirectorServiceImpl(directorStorage);
        assertThrows(NotFoundException.class, () -> directorService.delete(id));
    }
}