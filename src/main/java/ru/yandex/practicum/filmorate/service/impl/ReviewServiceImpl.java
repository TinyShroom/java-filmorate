package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Override
    public Review add(Review review) {
        log.info("is review[id={}] exists check", review.getReviewId());
        if (!isUserExists(review.getUserId())) {
            throw new NotFoundException(String.format("user with id == %d not found", review.getUserId()));
        }
        log.info("review[id={}] exists", review.getReviewId());
        log.info("is film[id={}] exists check", review.getFilmId());
        if (!isFilmExists(review.getFilmId())) {
            throw new NotFoundException(String.format("film with id == %d not found", review.getFilmId()));
        }
        log.info("film[id={}] exists", review.getFilmId());
        review.setReviewId(reviewStorage.addAndReturnId(review));
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(review.getReviewId())
                .build());
        log.info("add review[id={}]", review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("is review[id={}] exists check", review.getReviewId());
        if (!reviewStorage.isReviewExists(review.getReviewId())) {
            throw new NotFoundException(String.format("review with id == %d not found", review.getReviewId()));
        }
        log.info("review[id={}] exists", review.getReviewId());
        Review oldReview = reviewStorage.getById(review.getReviewId());
        Review newReview = reviewStorage.update(review);
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(oldReview.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(oldReview.getReviewId())
                .build());
        log.info("review[id={}] updated to {}", review.getReviewId(), newReview);
        return newReview;
    }

    @Override
    public void delete(long id) {
        log.info("is review[id={}] exists check", id);
        if (!reviewStorage.isReviewExists(id)) {
            throw new NotFoundException(String.format("review with id == %d not found", id));
        }
        log.info("review[id={}] exists", id);
        Review review = reviewStorage.getById(id);
        feedStorage.recordEvent(Feed.builder()
                .timestamp(new Date().getTime())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(review.getReviewId())
                .build());
        reviewStorage.delete(id);
        log.info("review[id={}] deleted", id);
    }

    @Override
    public Review getById(long id) {
        return reviewStorage.getById(id);
    }

    @Override
    public Collection<Review> getAll(Long filmId, long count) {
        if (filmId == null) {
            log.info("film is not indicated");
            return reviewStorage.getAll(count);
        }
        log.info("film indicated, filmId=[{}]", filmId);
        return reviewStorage.getFilmReviews(filmId, count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        log.info("is review[id={}] exists check", reviewId);
        if (!reviewStorage.isReviewExists(reviewId)) {
            throw new NotFoundException(String.format("review with id == %d not found", reviewId));
        }
        log.info("review[id={}] exists", reviewId);
        if (!isUserExists(userId)) {
            throw new NotFoundException(String.format("user with id == %d not found", userId));
        }
        log.info("user[id={}] exists", userId);
        reviewStorage.addLike(reviewId, userId);
        log.info("like on review[id={}] from user[id={}] deleted", reviewId, userId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        log.info("is review[id={}] exists check", reviewId);
        if (!reviewStorage.isReviewExists(reviewId)) {
            throw new NotFoundException(String.format("review with id == %d not found", reviewId));
        }
        log.info("review[id={}] exists", reviewId);
        if (!isUserExists(userId)) {
            throw new NotFoundException(String.format("user with id == %d not found", userId));
        }
        log.info("user[id={}] exists", userId);
        reviewStorage.addDislike(reviewId, userId);
        log.info("dislike on review[id={}] from user[id={}] deleted", reviewId, userId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        log.info("is review[id={}] exists check", reviewId);
        if (!reviewStorage.isReviewExists(reviewId)) {
            throw new NotFoundException(String.format("review with id == %d not found", reviewId));
        }
        log.info("review[id={}] exists", reviewId);
        if (!isUserExists(userId)) {
            throw new NotFoundException(String.format("user with id == %d not found", userId));
        }
        log.info("user[id={}] exists", userId);
        reviewStorage.deleteLike(reviewId, userId);
        log.info("like on review[id={}] from user[id={}] deleted", reviewId, userId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        log.info("is review[id={}] exists check", reviewId);
        if (!reviewStorage.isReviewExists(reviewId)) {
            throw new NotFoundException(String.format("review with id == %d not found", reviewId));
        }
        log.info("review[id={}] exists", reviewId);
        if (!isUserExists(userId)) {
            throw new NotFoundException(String.format("user with id == %d not found", userId));
        }
        log.info("user[id={}] exists", userId);
        reviewStorage.deleteDislike(reviewId, userId);
        log.info("dislike on review[id={}] from user[id={}] deleted", reviewId, userId);
    }

    private boolean isFilmExists(long id) {
        log.info("is film[id={}] exists check", id);
        return filmStorage.findById(id).isPresent();
    }

    private boolean isUserExists(long id) {
        log.info("is user[id={}] exists check", id);
        return userStorage.findById(id).isPresent();
    }
}