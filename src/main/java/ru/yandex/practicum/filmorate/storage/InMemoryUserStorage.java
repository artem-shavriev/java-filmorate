package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage extends IdGenerator implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        log.info("Получен список пользователей.");
        return users.values();
    }

    public void userValidator(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Электронная почта уже используется");
            }
        }
        for (User u: users.values()) {
            if (u.getLogin().equals(user.getLogin())) {
                throw new ValidationException("Такой логин уже существует.");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не передано, его заменит логин пользователя.");
        }
    }

    public User addUser(User user) {
        userValidator(user);
        if (user.getId() == null) {
            user.setId(getNextId(users));
        }
        users.put(user.getId(), user);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            userValidator(newUser);
            User oldUser = users.get(newUser.getId());

            oldUser.setName(newUser.getName());
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
