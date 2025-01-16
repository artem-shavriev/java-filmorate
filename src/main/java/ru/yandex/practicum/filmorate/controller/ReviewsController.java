package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.dto.NewReviews;
import ru.yandex.practicum.filmorate.storage.dto.ReviewsDto;
import ru.yandex.practicum.filmorate.storage.dto.UpdateReviews;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {

    private final ReviewsService reviewsService;

    @PostMapping
    public ReviewsDto createReviews(@Valid @RequestBody NewReviews newReviews) {
        return reviewsService.createReviews(newReviews);
    }

    @PutMapping
    public ReviewsDto updateReviews(@Valid @RequestBody UpdateReviews updateReviews) {
        return reviewsService.updateReviews(updateReviews);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewsById(@PathVariable Integer id) {
        reviewsService.deleteReviews(id);
    }

    @GetMapping("/{id}")
    public ReviewsDto getReviewsById(@PathVariable Integer id) {
        return reviewsService.getReviewsById(id);
    }

    @GetMapping
    public List<ReviewsDto> getAllReviewsAndAllReviewsFilmById(@RequestParam(required = false, value = "filmId") Integer filmId,
                                                               @RequestParam(value = "count", defaultValue = "10") Integer count) {
        return reviewsService.getAllReviews(filmId, count);
    }

    @PutMapping("/{reviewsId}/like/{userId}")
    public void addLikeReviews(@PathVariable Integer reviewsId, @PathVariable Integer userId) {
        reviewsService.addLikeAndDislikeReviews(reviewsId, userId, "like");
    }

    @PutMapping("/{reviewsId}/dislike/{userId}")
    public void addDislikeReviews(@PathVariable Integer reviewsId, @PathVariable Integer userId) {
        reviewsService.addLikeAndDislikeReviews(reviewsId, userId, "dislike");
    }

    @DeleteMapping("/{reviewsId}/like/{userId}")
    public void deleteLikeReviews(@PathVariable Integer reviewsId, @PathVariable Integer userId) {
        reviewsService.deleteLikeAndDislikeReviews(reviewsId, userId);
    }

    @DeleteMapping("/{reviewsId}/dislike/{userId}")
    public void deleteDislikeReviews(@PathVariable Integer reviewsId, @PathVariable Integer userId) {
        reviewsService.deleteLikeAndDislikeReviews(reviewsId, userId);
    }
}
