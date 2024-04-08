package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbUserStorageTest {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private User user;
    private User secondUser;
    private DbUserStorage userStorage;

    @BeforeEach
    public void init() {
        userStorage = new DbUserStorage(jdbcTemplate);
        user = new User(10, "user@mail.com", "user_login", "user_name",
                LocalDate.of(2000, 5, 3), new HashSet<>());
        secondUser = new User(2, "newuser@mail.com", "new_user_login", "new_user_name",
                LocalDate.of(2001, 6, 4), new HashSet<>());
    }

    @Test
    public void createUserSuccess() {
        var returnedUser = userStorage.create(user);
        assertThat(returnedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void updateUserNotFound() {
        assertThat(userStorage.update(user)).isEmpty();
    }

    @Test
    public void updateUserSuccess() {
        var id = userStorage.create(user).getId();
        var newUser = new User(id, "newuser@mail.com", "new_user_login", "new_user_name",
                LocalDate.of(2001, 6, 4), new HashSet<>());
        var returnedUser = userStorage.update(newUser);
        assertThat(returnedUser.get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void findAllEmpty() {
        assertThat(userStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void findAllNotEmpty() {
        userStorage.create(user);
        userStorage.create(secondUser);
        assertThat(userStorage.findAll())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    public void findByIdNotFound() {
        assertThat(userStorage.findById(10L)).isEmpty();
    }

    @Test
    public void findByIdSuccess() {
        var id = userStorage.create(user).getId();
        user.setId(id);
        assertThat(userStorage.findById(id).get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void getFriendsEmpty() {
        var userId = userStorage.create(user).getId();
        assertThat(userStorage.getFriends(userId))
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void addAndGetFriendsSuccess() {
        var userId = userStorage.create(user).getId();
        var friendId = userStorage.create(secondUser).getId();
        userStorage.addFriends(userId, friendId);
        assertThat(userStorage.getFriends(userId))
                .isNotNull()
                .hasSize(1);
        assertThat(userStorage.getFriends(friendId))
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void getCommonFriendsEmpty() {
        var userId = userStorage.create(user).getId();
        var secondId = userStorage.create(secondUser).getId();
        var thirdUser  = new User(3, "thirduser@mail.com", "user_login3", "user_name",
                LocalDate.of(2002, 7, 5), new HashSet<>());
        var thirdId = userStorage.create(thirdUser).getId();
        assertThat(userStorage.getCommonFriends(userId, secondId))
                .isNotNull()
                .hasSize(0);
        userStorage.addFriends(userId, secondId);
        userStorage.addFriends(secondId, thirdId);
        assertThat(userStorage.getCommonFriends(userId, secondId))
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void getCommonFriendsNotEmpty() {
        var userId = userStorage.create(user).getId();
        var secondId = userStorage.create(secondUser).getId();
        var thirdUser  = new User(3, "thirduser@mail.com", "user_login3", "user_name",
                LocalDate.of(2002, 7, 5), new HashSet<>());
        var thirdId = userStorage.create(thirdUser).getId();
        userStorage.addFriends(userId, thirdId);
        userStorage.addFriends(secondId, thirdId);
        assertThat(userStorage.getCommonFriends(userId, secondId))
                .isNotNull()
                .hasSize(1);
    }
}