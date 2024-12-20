package ru.yandex.practicum.filmorate.storage.dal.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    FriendsIdsStorage friendsIdsRepository;

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getLong("USER_ID"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setEmail(resultSet.getString("EMAIL"));

        LocalDate birthday = resultSet.getDate("BIRTHDAY").toLocalDate();
        user.setBirthday(birthday);

        if (resultSet.getString("NAME") == null) {
            user.setName(user.getLogin());
        } else {
            user.setName(resultSet.getString("NAME"));
        }

        Set<Long> friendsIdsSet = new HashSet<>();

        List<FriendsIds> friendsIdsObjects = friendsIdsRepository.findUserFriends(user.getId());
        friendsIdsObjects.stream()
                .forEach(friend -> friendsIdsSet.add(friend.getFriendId()));
        user.setFriendsId(friendsIdsSet);

        return user;
    }
}
