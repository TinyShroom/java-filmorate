package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (:email, :login, :name, :birthday);";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, namedParameters, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        String sqlUpdateQuery =
                "UPDATE users " +
                "SET " +
                    "email = :email, " +
                    "login = :login, " +
                    "name = :name, " +
                    "birthday = :birthday " +
                "WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("id", user.getId());

        var value = jdbcTemplate.update(sqlUpdateQuery, namedParameters);
        if (value < 1) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery =
                "SELECT * " +
                "FROM users;";
        return jdbcTemplate.query(sqlQuery, this::makeUsers);
    }

    @Override
    public Optional<User> findById(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        String sqlReadQuery = "SELECT * FROM users WHERE id = :id";
        return jdbcTemplate.query(sqlReadQuery, namedParameters, this::makeUser);
    }

    @Override
    public void delete(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        jdbcTemplate.update("DELETE FROM users WHERE id = :id", namedParameters);
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        jdbcTemplate.update("MERGE INTO friend(user_id, friend_id) values (:user_id, :friend_id)", namedParameters);
    }

    @Override
    public void deleteFriends(Long userId, Long friendId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("friend_id", friendId);
        jdbcTemplate.update("DELETE FROM friend WHERE user_id = :user_id AND friend_id = :friend_id", namedParameters);
    }

    @Override
    public List<User> getFriends(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);
        String sqlQuery =
                        "SELECT u.id,\n" +
                        "       u.email,\n" +
                        "       u.login,\n" +
                        "       u.name,\n" +
                        "       u.birthday\n" +
                        "FROM friend AS f\n" +
                        "JOIN users AS u ON f.friend_id = u.id\n" +
                        "WHERE f.user_id = :id;";
        return jdbcTemplate.query(sqlQuery, namedParameters, this::makeUsers);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long secondId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("second_id", secondId);
        String sqlQuery =
                "SELECT u.id,\n" +
                        "    u.email,\n" +
                        "    u.login,\n" +
                        "    u.name,\n" +
                        "    u.birthday\n" +
                        "FROM friend AS f\n" +
                        "JOIN users AS u ON f.friend_id = u.id\n" +
                        "WHERE f.user_id = :id AND f.friend_id IN (\n" +
                        "    SELECT us.id\n" +
                        "    FROM friend AS fs\n" +
                        "    JOIN users AS us ON fs.friend_id = us.id\n" +
                        "    WHERE fs.user_id = :second_id\n" +
                        ");";
        return jdbcTemplate.query(sqlQuery, namedParameters, this::makeUsers);
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User makeUsers(ResultSet resultSet, int rowNum) throws SQLException {
        var birthday = resultSet.getDate("birthday");
        var birthdayLocalDate = birthday == null ? null : birthday.toLocalDate();
        return new User(resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                birthdayLocalDate,
                new HashSet<>()
        );
    }

    private Optional<User> makeUser(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            var birthday = resultSet.getDate("birthday");
            var birthdayLocalDate = birthday == null ? null : birthday.toLocalDate();
            return Optional.of(new User(resultSet.getLong("id"),
                    resultSet.getString("email"),
                    resultSet.getString("login"),
                    resultSet.getString("name"),
                    birthdayLocalDate,
                    new HashSet<>()
            ));
        }
        return Optional.empty();
    }

    @Override
    public Long findUserWithSimilarLikes(Long userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("user_id", userId);
        String sqlQuery = "SELECT fl2.user_id " +
                "FROM FILM_LIKES AS fl1, FILM_LIKES AS fl2 " +
                "WHERE fl1.film_id = fl2.film_id " +
                "AND fl1.user_id = :user_id AND fl1.user_id <> fl2.user_id " +
                "GROUP BY fl1.user_id, fl2.user_id " +
                "ORDER BY count(*) desc limit 1";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, namedParameters, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}