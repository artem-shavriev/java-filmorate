package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikesFromUsers {
    private Integer filmId;
    private Integer userId;
}
