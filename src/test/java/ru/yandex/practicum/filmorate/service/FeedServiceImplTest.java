package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.impl.FeedServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbFeedStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbUserStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(FeedService.class)
public class FeedServiceImplTest {
    @MockBean
    private DbFeedStorage feedStorage;
    @MockBean
    private DbUserStorage userStorage;
    @MockBean
    private FeedService feedService;

    @Test
    public void userNotFound() {
        Long id = -1L;
        feedService = new FeedServiceImpl(feedStorage, userStorage);
        assertThrows(NotFoundException.class, () -> feedService.getUserFeed(id));
    }
}
