package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class NewReviews {

    @NotNull
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    Integer useful;
}
