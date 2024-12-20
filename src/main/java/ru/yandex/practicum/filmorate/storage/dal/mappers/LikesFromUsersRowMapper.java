package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikesFromUsersRowMapper implements RowMapper<LikesFromUsers> {
    @Override
    public LikesFromUsers mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LikesFromUsers likesFromUsers = new LikesFromUsers();

        likesFromUsers.setFilmId(resultSet.getLong("FILM_ID"));
        likesFromUsers.setUserId(resultSet.getLong("USER_ID"));

        return likesFromUsers;
    }
}
