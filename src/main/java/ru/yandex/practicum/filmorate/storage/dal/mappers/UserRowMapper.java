package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final FriendsIdsStorage friendsIdsStorage;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt("USER_ID"));
        user.setName(resultSet.getString("NAME"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setLogin(resultSet.getString("LOGIN"));

        if (resultSet.getTimestamp("BIRTHDAY") != null) {
            Timestamp birthday = resultSet.getTimestamp("BIRTHDAY");
            user.setBirthday(birthday.toLocalDateTime().toLocalDate());
        }

        Set<Integer> friendsIdsSet = new HashSet<>();

        List<FriendsIds> friendsIdsObjects = friendsIdsStorage.findUserFriends(user.getId());
        friendsIdsObjects.stream()
                .forEach(friend -> friendsIdsSet.add(friend.getFriendId()));
        user.setFriendsId(friendsIdsSet);

        return user;
    }
}
