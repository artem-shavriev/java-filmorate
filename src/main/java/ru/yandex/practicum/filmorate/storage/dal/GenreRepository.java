package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
