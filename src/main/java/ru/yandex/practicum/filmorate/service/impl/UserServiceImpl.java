package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        return userStorage.create(user);
    }

    @Override
    public User changeUser(User user) {
        return userStorage.update(user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.findAll());
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("PUT friends: id equals friendId");
        }
        userStorage.addFriends(id, friendId);
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("DELETE friends: id equals friendId");
        }
        userStorage.deleteFriends(id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long secondId) {
        if (id.equals(secondId)) {
            throw new ValidationException("firstId equals friendId");
        }
        return userStorage.getCommonFriends(id, secondId);
    }
}
