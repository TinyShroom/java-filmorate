package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.ValidationException;
import java.util.ArrayList;
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
            throw new ValidationException("POST /users: invalid birthdate " + user.getBirthday());
        }
        user.setId(idGenerator());
        return userStorage.save(user);
    }

    public User changeUser(User user) {
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("PUT /users: invalid birthdate " + user.getBirthday());
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

    private long idGenerator() {
        return idCounter++;
    }

}
