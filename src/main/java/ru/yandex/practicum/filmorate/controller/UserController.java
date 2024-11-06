package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    public String userValidator(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return "Электронная почта должна быть указана.";
        }
        if (user.getEmail().indexOf("@") < 0) {
            return "Электронная почта должна должна содержать @";
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0) {
            return "Логин не может быть пустым и содержать пробелы.";
        }
        if (user.getBirthday().isAfter(Instant.now())) {
            return "Дата рождения не может быть в будущем.";
        }
        return "true";
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (!userValidator(user).equals("true")) {
            throw new ValidationException(userValidator(user));
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (!userValidator(newUser).equals("true")) {
                throw new ValidationException(userValidator(newUser));
            }

            User oldUser = users.get(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());

            return oldUser;
        }
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }
}
