package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository("feedDbStorage")
@RequiredArgsConstructor
public class DbFeedStorage implements FeedStorage {

    private final NamedParameterJdbcOperations jdbcTemplate;

    @Override
    public void recordEvent(Feed feed) {
        Long eventTypeId = getEventTypeId(feed.getEventType().name());
        Long operationId = getOperationId(feed.getOperation().name());
        String sqlQuery = "INSERT INTO feed(timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (:timestamp, :user_id, :event_type, :operation, :entity_id);";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("timestamp", new Date().getTime())
                .addValue("user_id", feed.getUserId())
                .addValue("event_type", eventTypeId)
                .addValue("operation", operationId)
                .addValue("entity_id", feed.getEntityId());
        jdbcTemplate.update(sqlQuery, namedParameters);
    }

    @Override
    public List<Feed> getFeed(Long userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", userId);
        String sqlQuery =
                "SELECT f.timestamp,\n" +
                        "    f.user_id,\n" +
                        "    et.name AS even_type_name,\n" +
                        "    o.name AS operation_name,\n" +
                        "    f.event_id,\n" +
                        "    f.entity_id\n" +
                        "FROM feed AS f\n" +
                        "INNER JOIN event_type et ON f.event_type = et.id\n" +
                        "INNER JOIN operation o ON f.operation = o.id\n" +
                        "WHERE f.user_id = :id\n" +
                        ";";
        return jdbcTemplate.query(sqlQuery, namedParameters, this::makeFeeds);
    }

    @Override
    public Long getEventTypeId(String eventType) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("type", eventType);
        String sqlReadQuery = "SELECT id FROM event_type WHERE name = :type";
        try {
            return jdbcTemplate.queryForObject(sqlReadQuery, namedParameters, this::makeEventTypeId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("event type : %s , not found", eventType));
        }
    }

    @Override
    public Long getOperationId(String operation) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("operation", operation);
        String sqlReadQuery = "SELECT id FROM operation WHERE name = :operation";
        try {
            return jdbcTemplate.queryForObject(sqlReadQuery, namedParameters, this::makeOperationId);
        } catch (DataAccessException e) {
            throw new NotFoundException(String.format("operation : %s , not found", operation));
        }
    }

    private Feed makeFeeds(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(resultSet.getLong("timestamp"))
                .userId(resultSet.getLong("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("even_type_name")))
                .operation(Operation.valueOf(resultSet.getString("operation_name")))
                .eventId(resultSet.getLong("event_id"))
                .entityId(resultSet.getLong("entity_id"))
                .build();
    }

    private Long makeEventTypeId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("id");
    }

    private Long makeOperationId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("id");
    }
}
