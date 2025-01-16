package ru.yandex.practicum.filmorate.storage.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReviewsDto {

    Integer reviewId;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;
}
