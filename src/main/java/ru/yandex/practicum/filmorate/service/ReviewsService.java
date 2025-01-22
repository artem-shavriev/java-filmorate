package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
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
    private final EventService eventService;

    public ReviewsDto createReviews(NewReviews newReviews) {
        validNotFoundFilm(newReviews.getFilmId());
        validNotFoundUser(newReviews.getUserId());

        ReviewsDto createdReview = reviewsDbStorage.createReviews(newReviews);

        eventService.createEvent(newReviews.getUserId(), EventType.REVIEW, EventOperation.ADD, createdReview.getReviewId());

        return createdReview;
    }


    public ReviewsDto updateReviews(UpdateReviews updateReviews) {
        validNotFoundReviews(updateReviews.getReviewId());
        validNotFoundFilm(updateReviews.getFilmId());
        validNotFoundUser(updateReviews.getUserId());

        ReviewsDto updatedReview = reviewsDbStorage.updateReviews(updateReviews);

        eventService.createEvent(updateReviews.getUserId(), EventType.REVIEW, EventOperation.UPDATE, updatedReview.getReviewId());

        return updatedReview;
    }

    public void deleteReviews(Integer id) {
        ReviewsDto reviewsDto = validNotFoundReviews(id);
        reviewsDbStorage.deleteReviewsById(id);
        eventService.createEvent(reviewsDto.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewsDto.getReviewId());
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

            eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, reviewsId);
        } else {
            reviewsDbStorage.updateLikeAndDislikeReviews(reviewsId, userId, mark);
            eventService.createEvent(userId, EventType.LIKE, EventOperation.UPDATE, reviewsId);
        }
    }

    public void deleteLikeAndDislikeReviews(Integer reviewsId, Integer userId) {
        reviewsDbStorage.deleteLikeAndDislikeReviews(reviewsId, userId);

        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, reviewsId);
    }

    private ReviewsDto validNotFoundReviews(Integer reviewsId) {
        Optional<ReviewsDto> reviews = Optional.ofNullable(reviewsDbStorage.getReviewsById(reviewsId));
        if (reviews.isEmpty()) {
            throw new NotFoundException("Отзыв с таким id " + reviewsId + " не найден");
        } else {
            return reviews.get();
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
