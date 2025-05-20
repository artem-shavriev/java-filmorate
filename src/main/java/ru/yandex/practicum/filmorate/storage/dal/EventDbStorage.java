
package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.EventRowMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int id) {
        final String sql = "SELECT * " +
                "FROM feed " +
                "WHERE user_id = ? ";

        return jdbcTemplate.query(sql, new EventRowMapper(), id);
    }

    @Override
    public void createEvent(Event event) {
        final String sql = "insert into feed (user_id, timestamp, event_type, operation, entity_id) " +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );
    }
}
