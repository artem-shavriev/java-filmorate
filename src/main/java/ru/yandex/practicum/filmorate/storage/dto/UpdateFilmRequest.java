package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotBlank
    private String name;
    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов.")
    private String description;
    @NotNull
    private Date releaseDate;
    @NotNull
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    private Mpa mpa;
    private Set<Long> likesFromUsers = new HashSet<>();
    private List<Genre> genres;

    public boolean hasName() {
        return name != null || !name.isEmpty();
    }

    public boolean hasDescription() {
        return description != null || !description.isEmpty();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return description != null || !description.isEmpty();
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasLikesFromUsers() {
        return likesFromUsers != null;
    }

    public boolean hasGenres() {
        return genres != null;
    }
}
