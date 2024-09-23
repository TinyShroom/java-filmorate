package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    long addAndReturnId(Review review);

    Review update(Review review);

    void delete(long id);

    Review getById(long id);

    Collection<Review> getAll(long count);

    Collection<Review> getFilmReviews(long filmId, long count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    public void deleteLike(long reviewId, long userId);

    public void deleteDislike(long reviewId, long userId);

    boolean isReviewExists(long id);
}