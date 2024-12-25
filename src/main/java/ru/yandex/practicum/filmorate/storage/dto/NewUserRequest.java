package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private Long id;
    @NotBlank
    @Email
    private String email;
    private String login;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Past
    private LocalDate birthday;
}
