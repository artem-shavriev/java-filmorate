package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;

import java.util.List;

@Repository
public class LikesFromUsersStorage extends BaseStorage<LikesFromUsers> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM LIKES_FROM_USERS";
    private static final String INSERT_QUERY = "INSERT INTO LIKES_FROM_USERS (FILM_ID, USER_ID)" +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM LIKES_FROM_USERS WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String DELETE_BY_FILM_ID_QUERY = "DELETE FROM LIKES_FROM_USERS WHERE FILM_ID = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM LIKES_FROM_USERS WHERE FILM_ID = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM LIKES_FROM_USERS WHERE USER_ID = ?";

    public LikesFromUsersStorage(JdbcTemplate jdbc, RowMapper<LikesFromUsers> mapper) {
        super(jdbc, mapper);
    }

    public List<LikesFromUsers> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public List<LikesFromUsers> findLikesByFilmId(Integer filmId) {
        return findMany(FIND_BY_FILM_ID_QUERY, filmId);
    }

    public List<LikesFromUsers> findLikesByUserId(Integer userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    public LikesFromUsers addLike(Integer filmId, Integer userId) {
        LikesFromUsers likes = new LikesFromUsers();

        likes.setFilmId(filmId);
        likes.setUserId(userId);

        update(INSERT_QUERY, filmId, userId);

        return likes;
    }

    public boolean deleteLike(Integer filmId, Integer userId) {
        return delete(DELETE_QUERY, filmId, userId);
    }

    public boolean deleteLikesFromUsersByFilmId(Integer filmId) {
        return delete(DELETE_BY_FILM_ID_QUERY, filmId);
    }
}
