package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class DbReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long addAndReturnId(Review review) {
        String sqlQuery = "insert into review (content, is_positive, user_id, film_id) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "update review set content = ?, is_positive = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public void delete(long id) {
        String sqlQuery = "delete from review where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review getById(long id) {
        String sqlQuery = "select * from review where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
    }

    @Override
    public Collection<Review> getAll(long count) {
        String sqlQuery = "select * from review order by useful desc limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public Collection<Review> getFilmReviews(long filmId, long count) {
        String sqlQuery = "select * from review where film_id = ? order by useful desc limit ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sqlQuery = "insert into review_likes (review_id, user_id, is_like) values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, true);
        increaseUseful(reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String sqlQuery = "insert into review_likes (review_id, user_id, is_like) values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, false);
        decreaseUseful(reviewId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String sqlQuery = "delete from review_likes where review_id = ? and user_id = ? ";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        decreaseUseful(reviewId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        String sqlQuery = "delete from review_likes where review_id = ? and user_id = ? ";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
        increaseUseful(reviewId);
    }

    private void increaseUseful(long reviewId) {
        String sqlQuery = "update review set useful = useful + 1 where id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    private void decreaseUseful(long reviewId) {
        String sqlQuery = "update review set useful = useful - 1 where id = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public boolean isReviewExists(long id) {
        String sqlQuery = "select count(*) from review where id = ?";
        Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        return count != null && count > 0;
    }

    private Review mapRowToReview(ResultSet resultSet, Integer rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}