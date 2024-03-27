package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DatabaseConstraintException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlUpdateQuery =
                "UPDATE users " +
                "SET " +
                    "email = ?, " +
                    "login = ?, " +
                    "name = ?, " +
                    "birthdate = ? " +
                "WHERE id = ?";

        var value = jdbcTemplate.update(sqlUpdateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthdate(),
                user.getId());
        if (value < 1) {
            throw new NotFoundException(String.format("user with id %d not found", user.getId()));
        }
        String sqlReadQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlReadQuery, this::makeUser, user.getId());
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery =
                "SELECT * " +
                "FROM users;";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User findById(Long id) {
        String sqlReadQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlReadQuery, this::makeUser, id);
    }

    @Override
    public void addFriends(Long id, Long friendId) {
        try {
            jdbcTemplate.update("INSERT INTO friend(user_id, friend_id) values (?, ?)", id, friendId);
        } catch (Exception e) {
            throw new DatabaseConstraintException("user not found or already friend");
        }
    }

    @Override
    public void deleteFriends(Long id, Long friendId) {
        var result = jdbcTemplate.update("DELETE FROM friend WHERE user_id = ? AND friend_id = ?", id, friendId);
        if (result < 1) {
            throw new NotFoundException("friend not found");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        String sqlQuery =
                        "SELECT u.id,\n" +
                        "       u.email,\n" +
                        "       u.login,\n" +
                        "       u.name,\n" +
                        "       u.birthdate\n" +
                        "FROM friend AS f\n" +
                        "JOIN users AS u ON f.friend_id = u.id\n" +
                        "WHERE f.user_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::makeUser, id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        return null;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthdate", user.getBirthdate());
        return values;
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        var birthdate = resultSet.getDate("birthdate");
        var birthdateLocalDate = birthdate == null ? null : birthdate.toLocalDate();
        return new User(resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                birthdateLocalDate,
                new HashSet<>()
        );
    }
    private boolean isUserExist(Long id) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id).next();
    }
}
