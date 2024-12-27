package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    private String login;
    @NotBlank
    private String name;
    @Past
    private LocalDate birthday;
}
