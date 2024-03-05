package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private long idCounter;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        this.idCounter = 1;
    }
    public User addUser(User user) {
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("POST /users: birthdate must not be in the future");
        }
        user.setId(idGenerator());
        return userStorage.save(user);
    }

    public User changeUser(User user) {
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("PUT /users: birthdate must not be in the future");
        }
        var oldUser = userStorage.findById(user.getId());
        if (oldUser == null) {
            throw new NotFoundException(String.format("PUT /users: user with id %d not found", user.getId()));
        }
        return userStorage.save(user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.findAll());
    }

    public void addFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("PUT friends: id equals friendId");
        }
        var user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException(String.format("PUT friends: user id %d not found", id));
        }
        var friend = userStorage.findById(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("PUT friends: friend id %d not found", friendId));
        }
        user.getFriends().add(friendId);
    }

    public void deleteFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("DELETE friends: id equals friendId");
        }
        var user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException(String.format("DELETE friends: user id %d not found", id));
        }
        var friend = userStorage.findById(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("DELETE friends: friend id %d not found", friendId));
        }
        user.getFriends().remove(friendId);
    }

    public List<User> getFriends(Long id) {
        var user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException(String.format("DELETE friends: user id %d not found", id));
        }
        List<User> friends = new ArrayList<>();
        for (var friendId: user.getFriends()) {
            friends.add(userStorage.findById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        var user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException(String.format("DELETE friends: user id %d not found", id));
        }
        var otherUser = userStorage.findById(otherId);
        if (otherUser == null) {
            throw new NotFoundException(String.format("DELETE friends: other id %d not found", id));
        }
        var commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(otherUser.getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (var friendId: commonIds) {
            commonFriends.add(userStorage.findById(friendId));
        }
        return commonFriends;
    }

    private long idGenerator() {
        return idCounter++;
    }

}
