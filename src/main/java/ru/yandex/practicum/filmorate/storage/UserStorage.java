package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    public User addUser(@Valid @RequestBody User user);

    public User updateUser(@Valid @RequestBody User newUser);
}
