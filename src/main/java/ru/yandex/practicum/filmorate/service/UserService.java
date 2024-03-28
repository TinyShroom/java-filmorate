package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User changeUser(User user);

    List<User> getUsers();

    void addFriends(Long id, Long friendId);

    void deleteFriends(Long id, Long friendId);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long secondId);
}
