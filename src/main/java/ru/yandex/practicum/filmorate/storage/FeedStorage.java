package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void recordEvent(Feed feed);

    List<Feed> getFeed(Long userId);

    Long getEventTypeId(String eventType);

    Long getOperationId(String operation);
}
