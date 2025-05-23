package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewsMark;
import ru.yandex.practicum.filmorate.storage.dal.mappers.ReviewsMarkRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.ReviewsRowMapper;
import ru.yandex.practicum.filmorate.storage.dto.NewReviews;
import ru.yandex.practicum.filmorate.storage.dto.ReviewsDto;
import ru.yandex.practicum.filmorate.storage.dto.UpdateReviews;

import java.sql.PreparedStatement;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewsDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewsRowMapper reviewsRowMapper;
    private final ReviewsMarkRowMapper reviewsMarkRowMapper;

    public ReviewsDto createReviews(NewReviews newReviews) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int reviewsId;
        String createReviewsSql = "INSERT INTO REVIEWS(CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(createReviewsSql, new String[]{"REVIEWS_ID"});
            stmt.setString(1, newReviews.getContent());
            stmt.setBoolean(2, (newReviews.getIsPositive()));
            stmt.setInt(3, newReviews.getUserId());
            stmt.setInt(4, newReviews.getFilmId());
            return stmt;
        }, keyHolder);
        if (Objects.nonNull(keyHolder.getKey())) {
            reviewsId = keyHolder.getKey().intValue();
            log.info("Отзыв с id {} добавлен в таблицу", reviewsId);
        } else {
            throw new NotFoundException("Ошибка добавления отзыва в таблицу");
        }
        return ReviewsDto.builder()
                .reviewId(reviewsId)
                .content(newReviews.getContent())
                .isPositive(newReviews.getIsPositive())
                .userId(newReviews.getUserId())
                .filmId(newReviews.getFilmId())
                .useful(0)
                .build();
    }

    public ReviewsDto updateReviews(UpdateReviews updateReviews) {
        String updateReviewsSql = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? " +
                " WHERE REVIEWS_ID = ?";

        jdbcTemplate.update(updateReviewsSql,
                updateReviews.getContent(),
                updateReviews.getIsPositive(),
                updateReviews.getReviewId());

        log.info("Обновлен существующий отзыв: {}", updateReviews.getReviewId());
        return getReviewsById(updateReviews.getReviewId());
    }

    public void deleteReviewsById(Integer id) {
        String deleteSql = "DELETE FROM REVIEWS WHERE REVIEWS_ID = ?";
        jdbcTemplate.update(deleteSql, id);
    }

    public ReviewsDto getReviewsById(Integer id) {
        String getReviewsById = "SELECT * FROM REVIEWS WHERE REVIEWS_ID = ?";
        try {
            ReviewsDto result = jdbcTemplate.queryForObject(getReviewsById, reviewsRowMapper, id);
            log.info("Отзыв получен по id {}", id);
            return result;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id " + id + " не найден");
        }
    }

    public List<ReviewsDto> getAllReviewsByFilmId(Integer filmId, Integer count) {
        Comparator<ReviewsDto> reviewsComparator = Comparator.comparingInt(ReviewsDto::getUseful);
        String getAllReviewsByFilmId = "SELECT * FROM REVIEWS WHERE FILM_ID = ? limit ?";
        return jdbcTemplate.query(getAllReviewsByFilmId, reviewsRowMapper, filmId, count)
                .stream()
                .sorted(reviewsComparator.reversed())
                .toList();
    }

    public List<ReviewsDto> getAllReviews(Integer count) {
        Comparator<ReviewsDto> reviewsComparator = Comparator.comparingInt(ReviewsDto::getUseful);
        String getAllReviews = "SELECT * FROM REVIEWS limit ?";
        return jdbcTemplate.query(getAllReviews, reviewsRowMapper, count)
                .stream()
                .sorted(reviewsComparator.reversed())
                .toList();
    }

    public void addLikeAndDislikeReviews(Integer reviewsId, Integer userId, String mark) {
        String addLikeSql = "INSERT INTO REVIEWS_MARK(REVIEWS_ID, USER_ID, MARK) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(addLikeSql, reviewsId, userId, mark);
        if (rows == 0) {
            throw new DuplicatedDataException("Не удалось записать данные в таблицу");
        }
    }

    public void updateLikeAndDislikeReviews(Integer reviewsId, Integer userId, String mark) {
        String addLikeSql = "UPDATE REVIEWS_MARK SET MARK = ? WHERE REVIEWS_ID = ? AND USER_ID = ?";
        int rows = jdbcTemplate.update(addLikeSql, mark, reviewsId, userId);
        if (rows == 0) {
            throw new DuplicatedDataException("Не удалось обновить данные в таблице");
        }
    }

    public void deleteLikeAndDislikeReviews(Integer reviewsId, Integer userId) {
        String deleteSql = "DELETE FROM REVIEWS_MARK WHERE REVIEWS_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(deleteSql, reviewsId, userId);
    }

    public Integer getUseful(Integer reviewsId) {
        String getLikeSql = "SELECT COUNT(MARK) FROM REVIEWS_MARK WHERE REVIEWS_ID = ? AND MARK = 'like'";
        Integer like = jdbcTemplate.queryForObject(getLikeSql, Integer.class, reviewsId);
        String getDislikeSql = "SELECT COUNT(MARK) FROM REVIEWS_MARK WHERE REVIEWS_ID = ? AND MARK = 'dislike'";
        Integer dislike = jdbcTemplate.queryForObject(getDislikeSql, Integer.class, reviewsId);
        return like - dislike;
    }

    public ReviewsMark getReviewsMarkById(Integer reviewsId, Integer userId) {
        String getReviewsMark = "SELECT * FROM REVIEWS_MARK WHERE REVIEWS_ID = ? AND USER_ID = ?";
        try {
            ReviewsMark result = jdbcTemplate.queryForObject(getReviewsMark, reviewsMarkRowMapper, reviewsId, userId);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
