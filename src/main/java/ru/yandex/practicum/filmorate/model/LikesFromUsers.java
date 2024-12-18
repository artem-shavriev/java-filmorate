package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class LikesFromUsers {
    private long filmId;
    private long userId;
}
