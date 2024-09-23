package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@ContextConfiguration(classes = FilmorateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFeedStorageTest {
    private final NamedParameterJdbcOperations jdbcTemplate;
    private DbFeedStorage dbFeedStorage;
    private DbUserStorage dbUserStorage;

    private User user;

    @BeforeEach
    public void init() {
        dbFeedStorage = new DbFeedStorage(jdbcTemplate);
        dbUserStorage = new DbUserStorage(jdbcTemplate);
        user = new User(1L, "mail@mail.ru", "userName", "userLogin",
                LocalDate.of(1990, 1, 1), new HashSet<Long>());
    }

    @Test
    public void recordEventSuccess() {
        dbUserStorage.create(user);
        dbFeedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(user.getId())
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(1L)
                .build());
        assertThat(dbFeedStorage.getFeed(user.getId()))
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
    }

    @Test
    public void recordEventFailureWrongUserId() {
        Long wrongUserId = 999L;
        assertThatThrownBy(() -> dbFeedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(wrongUserId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(1L)
                .build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void getFeedSuccess() {
        dbUserStorage.create(user);
        dbFeedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(user.getId())
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(1L)
                .build());
        List<Feed> feed = dbFeedStorage.getFeed(user.getId());
        assertThat(feed)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(1);
        assertThat(feed.get(0))
                .isNotNull()
                .isInstanceOf(Feed.class);
    }

    @Test
    public void getFeedEmpty() {
        dbUserStorage.create(user);
        List<Feed> feed = dbFeedStorage.getFeed(user.getId());
        assertThat(feed)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(0);
    }

    @Test
    public void getEventTypeIdSuccess() {
        Long id = dbFeedStorage.getEventTypeId(EventType.LIKE.name());
        assertThat(id)
                .isNotNull()
                .isNotNegative();
    }

    @Test
    public void getEventTypeIdFailureWrongTypeName() {
        String wrongTypeName = "wrong name";
        assertThatThrownBy(() -> dbFeedStorage.getEventTypeId(wrongTypeName))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getOperationIdSuccess() {
        Long id = dbFeedStorage.getOperationId(Operation.ADD.name());
        assertThat(id)
                .isNotNull()
                .isNotNegative();
    }

    @Test
    public void getOperationIdFailureWrongOperationName() {
        String wrongOperationName = "wrong name";
        assertThatThrownBy(() -> dbFeedStorage.getOperationId(wrongOperationName))
                .isInstanceOf(NotFoundException.class);
    }
}
