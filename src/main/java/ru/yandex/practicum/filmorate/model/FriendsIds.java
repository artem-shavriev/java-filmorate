package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendsIds {
    private Integer id;
    private Integer userId;
    private Integer friendId;
}
