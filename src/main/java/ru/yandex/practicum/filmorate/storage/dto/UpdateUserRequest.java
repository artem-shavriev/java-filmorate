package ru.yandex.practicum.filmorate.storage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateUserRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @Past
    private Date birthday;

    public boolean hasEmail() {
        return email != null || !email.isEmpty();
    }

    public boolean hasLogin() {
        return login != null || !login.isEmpty();
    }

    public boolean hasName() {
        return name != null || !name.isEmpty();
    }

    public boolean hasBirthday() {
        return birthday != null;
    }
}
