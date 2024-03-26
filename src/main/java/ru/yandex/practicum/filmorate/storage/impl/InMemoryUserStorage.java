package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    private long idCounter;


    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public User create(User user) {
        user.setId(idGenerator());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        var user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("user id %d not found", id));
        }
        var friend = users.get(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("friend id %d not found", friendId));
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        var user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("user id %d not found", id));
        }
        var friend = users.get(friendId);
        if (friend == null) {
            throw new NotFoundException(String.format("friend id %d not found", friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        var user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("user id %d not found", id));
        }
        List<User> friends = new ArrayList<>();
        for (var friendId: user.getFriends()) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        var user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("user id %d not found", id));
        }
        var otherUser = users.get(otherId);
        if (otherUser == null) {
            throw new NotFoundException(String.format("other id %d not found", id));
        }
        var commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(otherUser.getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (var friendId: commonIds) {
            commonFriends.add(users.get(friendId));
        }
        return commonFriends;
    }

    private long idGenerator() {
        return ++idCounter;
    }
}
