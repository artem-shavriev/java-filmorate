package ru.yandex.practicum.filmorate.storage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String email;
    private String login;
    private String username;
    private LocalDate birthday;
    private Set<Long> friendsId = new HashSet<>();
}
