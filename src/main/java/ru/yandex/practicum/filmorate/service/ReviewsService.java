package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ReviewsMark;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.NewReviews;
import ru.yandex.practicum.filmorate.storage.dto.ReviewsDto;
import ru.yandex.practicum.filmorate.storage.dto.UpdateReviews;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsDbStorage reviewsDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public ReviewsDto createReviews(NewReviews newReviews) {
        validNotFoundFilm(newReviews.getFilmId());
        validNotFoundUser(newReviews.getUserId());
        return reviewsDbStorage.createReviews(newReviews);
    }

    public ReviewsDto updateReviews(UpdateReviews updateReviews) {
        validNotFoundReviews(updateReviews.getReviewId());
        validNotFoundFilm(updateReviews.getFilmId());
        validNotFoundUser(updateReviews.getUserId());
        return reviewsDbStorage.updateReviews(updateReviews);
    }

    public void deleteReviews(Integer id) {
        reviewsDbStorage.deleteReviewsById(id);
    }

    public ReviewsDto getReviewsById(Integer id) {
        return reviewsDbStorage.getReviewsById(id);
    }

    public List<ReviewsDto> getAllReviews(Integer filmId, Integer count) {
        if (filmId != null) {
            validNotFoundUser(filmId);
            return reviewsDbStorage.getAllReviewsByFilmId(filmId, count);
        } else {
            return reviewsDbStorage.getAllReviews(count);
        }
    }

    public void addLikeAndDislikeReviews(Integer reviewsId, Integer userId, String mark) {
        validNotFoundReviews(reviewsId);
        validNotFoundUser(userId);
        if (validNotFoundReviewsMark(reviewsId, userId).isEmpty()) {
            reviewsDbStorage.addLikeAndDislikeReviews(reviewsId, userId, mark);
        } else {
            reviewsDbStorage.updateLikeAndDislikeReviews(reviewsId, userId, mark);
        }
    }

    public void deleteLikeAndDislikeReviews(Integer reviewsId, Integer userId) {
        reviewsDbStorage.deleteLikeAndDislikeReviews(reviewsId, userId);
    }

    private void validNotFoundReviews(Integer reviewsId) {
        Optional<ReviewsDto> reviews = Optional.ofNullable(reviewsDbStorage.getReviewsById(reviewsId));
        if (reviews.isEmpty()) {
            throw new NotFoundException("Отзыв с таким id " + reviewsId + " не найден");
        }
    }

    private void validNotFoundFilm(Integer filmId) {
        Optional<Film> film = filmDbStorage.findById(filmId);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с таким id " + filmId + " не найден");
        }
    }

    private void validNotFoundUser(Integer userId) {
        Optional<User> user = userDbStorage.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id " + userId + " не найден");
        }
    }

    private Optional<ReviewsMark> validNotFoundReviewsMark(Integer reviewsId, Integer userId) {
        return Optional.ofNullable(reviewsDbStorage.getReviewsMarkById(reviewsId, userId));
    }
}
