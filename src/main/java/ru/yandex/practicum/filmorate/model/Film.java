package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов.")
    private String description;
    @NotNull
    private Date releaseDate;
    @NotNull
    @Positive (message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    private Set<Long> likesFromUsers = new HashSet<>();
    private List<Genre> genres;
    private Mpa mpa;
}
