package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;

import java.util.ArrayList;
import java.util.List;

public class LikesFromUsersRepository extends BaseRepository<LikesFromUsers> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM LIKES_FROM_USERS";
    private static final String INSERT_QUERY = "MERGE INTO LIKES_FROM_USERS (FILM_ID, USER_ID)" +
            "VALUES (?, ?) returning id";
    public LikesFromUsersRepository(JdbcTemplate jdbc, RowMapper<LikesFromUsers> mapper) {
        super(jdbc, mapper);
    }

    public List<LikesFromUsers> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public void saveLikesFromUsers(Film film) {
        List<Long> likesList = new ArrayList<>(film.getLikesFromUsers());

        for (Long like : likesList) {
            long id = insert(INSERT_QUERY,
                    film.getId(),
                    like
            );
        }
    }
}
