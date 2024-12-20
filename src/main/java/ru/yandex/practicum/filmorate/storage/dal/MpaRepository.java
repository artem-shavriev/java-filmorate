package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public class MpaRepository extends BaseRepository<Mpa>{
    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA WHERE MPA_ID = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Mpa> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
