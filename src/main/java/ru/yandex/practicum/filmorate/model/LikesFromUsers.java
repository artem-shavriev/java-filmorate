package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class LikesFromUsers {
    private Integer filmId;
    private Integer userId;
}
