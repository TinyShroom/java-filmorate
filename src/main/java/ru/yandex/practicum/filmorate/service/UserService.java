package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

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

    public List<User> getCommonFriends(Long id, Long secondId) {
        if (id.equals(secondId)) {
            throw new ValidationException("firstId equals friendId");
        }
        return userStorage.getCommonFriends(id, secondId);
    }

}
