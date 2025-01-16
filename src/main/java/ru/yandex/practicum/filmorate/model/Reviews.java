package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Reviews {
    Integer reviewsId;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;
}
