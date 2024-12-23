package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Date;

@Data
public class NewFilmRequest {
    @NotBlank
    private String name;
    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов.")
    private String description;
    @NotNull
    private Date releaseDate;
    @NotNull
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    private Mpa mpaRate;
}
