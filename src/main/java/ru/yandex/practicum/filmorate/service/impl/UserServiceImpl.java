package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Override
    public User addUser(User user) {
        return userStorage.create(user);
    }

    @Override
    public User changeUser(User user) {
        return userStorage.update(user)
                .orElseThrow(() -> new NotFoundException(String.format("user with id %d not found", user.getId()))
        );
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userStorage.findAll());
    }

    @Override
    public User getById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", id)));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("PUT friends: id equals friendId");
        }
        try {
            userStorage.addFriends(id, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("user or friend not found");
        }
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(id)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(friendId)
                .build());
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("DELETE friends: id equals friendId");
        }
        if (!isUserExist(id)) {
            throw new NotFoundException(String.format("user with id == %d not found", id));
        }
        if (!isUserExist(friendId)) {
            throw new NotFoundException(String.format("friend with id == %d not found", id));
        }
        userStorage.deleteFriends(id, friendId);
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(id)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(friendId)
                .build());
    }

    @Override
    public List<User> getFriends(Long id) {
        if (!isUserExist(id)) {
            throw new NotFoundException(String.format("user with id == %d not found", id));
        }
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long secondId) {
        if (id.equals(secondId)) {
            throw new ValidationException("firstId equals friendId");
        }
        return userStorage.getCommonFriends(id, secondId);
    }

    private boolean isUserExist(Long id) {
        return userStorage.findById(id).isPresent();
    }
}