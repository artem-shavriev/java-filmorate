package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен список пользователей.");
        return users.values();
    }

    public String userValidator(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return "Электронная почта должна быть указана.";
        }
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                return "Электронная почта уже используется";
            }
        }
        if (user.getEmail().indexOf("@") < 0) {
            return "Электронная почта должна содержать @";
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().indexOf(' ') >= 0) {
            return "Логин не может быть пустым и содержать пробелы.";
        }
        for (User u: users.values()) {
            if (u.getLogin().equals(user.getLogin())) {
                return "Такой логин уже существует.";
            }
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            return "Дата рождения не может быть в будущем.";
        }
        return "true";
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (!userValidator(user).equals("true")) {
            log.error(userValidator(user));
            throw new ValidationException(userValidator(user));
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не передано, его заменит логин пользователя.");
        }

        if (user.getId() == null) {
            user.setId(getNextId());
        }
        users.put(user.getId(), user);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (!userValidator(newUser).equals("true")) {
                log.error(userValidator(newUser));
                throw new ValidationException(userValidator(newUser));
            }

            User oldUser = users.get(newUser.getId());
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            } else {
                log.info("Логин добвален в качестве имени пользователя.");
                oldUser.setName(newUser.getLogin());
            }
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            log.info("Пользователь c id: {} обновлен", oldUser.getId());

            return oldUser;
        }
        log.error("User с id = {} не найден", newUser.getId());
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }
}
