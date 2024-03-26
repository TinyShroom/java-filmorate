package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("POST /users: birthdate must not be in the future");
        }
        return userStorage.create(user);
    }

    public User changeUser(User user) {
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("PUT /users: birthdate must not be in the future");
        }
        var oldUser = userStorage.findById(user.getId());
        if (oldUser == null) {
            throw new NotFoundException(String.format("PUT /users: user with id %d not found", user.getId()));
        }
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.findAll());
    }

    public void addFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("PUT friends: id equals friendId");
        }
        userStorage.addFriends(id, friendId);
    }

    public void deleteFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("DELETE friends: id equals friendId");
        }
        userStorage.deleteFriends(id, friendId);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

}
