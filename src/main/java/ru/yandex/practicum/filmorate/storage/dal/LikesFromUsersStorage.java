package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LikesFromUsersStorage extends BaseStorage<LikesFromUsers> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM LIKES_FROM_USERS";
    private static final String INSERT_QUERY = "MERGE INTO LIKES_FROM_USERS (FILM_ID, USER_ID)" +
            "VALUES (?, ?) returning id";
    private static final String DELETE_QUERY = "DELETE FROM LIKES_FROM_USERS WHERE FILM_ID = ? AND USER_ID = ?";

    public LikesFromUsersStorage(JdbcTemplate jdbc, RowMapper<LikesFromUsers> mapper) {
        super(jdbc, mapper);
    }

    public List<LikesFromUsers> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public void addLike(long filmId, long userId) {
        long id = insert(INSERT_QUERY, filmId, userId);
    }

    public boolean deleteLike(long filmId, long userId) {
        return delete(DELETE_QUERY, filmId, userId);
    }

    public void saveAllFilmsLikes(Film film) {
        List<Long> likesList = new ArrayList<>(film.getLikesFromUsers());

        for (Long like : likesList) {
            long id = insert(INSERT_QUERY,
                    film.getId(),
                    like
            );
        }
    }
}
