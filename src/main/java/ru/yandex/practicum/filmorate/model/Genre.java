package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Genre {
    @Min(value = 1, message = "Жанра с id меньше 1 нет")
    @Max(value = 6, message = "Жанра с id больше 6 нет")
    private long id;
    private String name;
}
