package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewsMark;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewsMarkRowMapper implements RowMapper<ReviewsMark> {
    @Override
    public ReviewsMark mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ReviewsMark.builder()
                .reviewsId(rs.getInt("REVIEWS_ID"))
                .userId(rs.getInt("USER_ID"))
                .mark(rs.getString("MARK"))
                .build();
    }
}
