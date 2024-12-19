package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendsIds;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsIdsMapper implements RowMapper<FriendsIds> {
    @Override
    public FriendsIds mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FriendsIds friendsIds = new FriendsIds();
        friendsIds.setFriendId(resultSet.getLong("FRIEND_ID"));
        friendsIds.setUserId(resultSet.getLong("USER_ID"));

        return friendsIds;
    }
}
