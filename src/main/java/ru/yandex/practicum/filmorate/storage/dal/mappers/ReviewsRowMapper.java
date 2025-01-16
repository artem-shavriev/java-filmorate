package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dal.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.ReviewsDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewsRowMapper implements RowMapper<ReviewsDto> {

    ReviewsDbStorage reviewsDbStorage;

    ReviewsRowMapper(@Lazy ReviewsDbStorage reviewsDbStorage) {
        this.reviewsDbStorage = reviewsDbStorage;
    }

    @Override
    public ReviewsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer useful = reviewsDbStorage.getUseful(rs.getInt("REVIEWS_ID"));
        return ReviewsDto.builder()
                .reviewId(rs.getInt("REVIEWS_ID"))
                .content(rs.getString("CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .userId(rs.getInt("USER_ID"))
                .filmId(rs.getInt("FILM_ID"))
                .useful(useful)
                .build();
    }
}
