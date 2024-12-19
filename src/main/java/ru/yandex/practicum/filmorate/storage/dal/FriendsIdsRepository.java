package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendsIds;

import java.util.List;

@Repository
public class FriendsIdsRepository extends BaseRepository<FriendsIds>{
    private static final String FIND_ALL_QUERY = "SELECT * FROM FRIENDS_IDS";
    private static final String INSERT_QUERY = "MERGE INTO FRIENDS_IDS (USER_ID, FRIEND_ID)" +
            "VALUES (?, ?) returning id";
    private static final String DELETE_QUERY = "DELETE FROM FRIENDS_IDS WHERE FRIEND_ID = ? AND USER_ID = ?";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT * FROM FRIENDS_IDS WHERE USER_ID = ?";

    public FriendsIdsRepository(JdbcTemplate jdbc, RowMapper<FriendsIds> mapper) {
        super(jdbc, mapper);
    }

    public List<FriendsIds> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public void addFriend(long friendId, long userId) {
        long id = insert(INSERT_QUERY, friendId, userId);
    }

    public List<FriendsIds> findUserFriends(long userId) {
        return findMany(FIND_USER_FRIENDS_QUERY, userId);
    }

    public boolean deleteLFriend(long friendId, long userId) {
        return delete(DELETE_QUERY, friendId, userId);
    }
}
