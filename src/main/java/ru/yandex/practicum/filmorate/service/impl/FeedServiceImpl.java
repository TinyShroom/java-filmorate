package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Override
    public List<Feed> getUserFeed(Long id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", id)));
        return feedStorage.getFeed(id);
    }
}
