package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;

import java.util.List;

public class LikesFromUsersRepository extends BaseRepository<LikesFromUsers> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM LIKES_FROM_USERS";

    public LikesFromUsersRepository(JdbcTemplate jdbc, RowMapper<LikesFromUsers> mapper) {
        super(jdbc, mapper);
    }

    public List<LikesFromUsers> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}
