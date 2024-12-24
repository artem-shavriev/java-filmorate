package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Mpa {
    @Min(value = 1, message = "Рейтинга с id меньше 1 нет")
    @Max(value = 5, message = "Рейтинга с id больше 5 нет")
    private long id;
}