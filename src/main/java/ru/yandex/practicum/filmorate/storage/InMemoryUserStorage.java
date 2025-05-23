package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage extends IdGenerator implements UserStorage {
    private final HashMap<Integer, User> usersMap = new HashMap<>();

    public HashMap<Integer, User> getUsersMap() {
        return usersMap;
    }

    public Collection<User> getUsers() {
        log.info("Получен список пользователей.");
        return usersMap.values();
    }

    public Collection<Integer> getUsersIds() {
        log.info("Получен список id пользователей.");
        return usersMap.keySet();
    }

    public User addUser(User user) {
        for (User u : usersMap.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Электронная почта уже используется");
            }
        }
        for (User u : usersMap.values()) {
            if (u.getLogin().equals(user.getLogin())) {
                throw new ValidationException("Такой логин уже существует.");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не передано, его заменит логин пользователя.");
        }
        if (user.getId() == null) {
            user.setId(getNextId(usersMap));
        }
        usersMap.put(user.getId(), user);
        log.info("Создан новый пользователь c id: {}", user.getId());

        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }
        if (usersMap.containsKey(newUser.getId())) {
            for (User u : usersMap.values()) {
                if (u.getEmail().equals(newUser.getEmail())) {
                    throw new ValidationException("Электронная почта уже используется");
                }
            }
            for (User u : usersMap.values()) {
                if (u.getLogin().equals(newUser.getLogin())) {
                    throw new ValidationException("Такой логин уже существует.");
                }
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.warn("Имя не передано, его заменит логин пользователя.");
            }
            User oldUser = usersMap.get(newUser.getId());

            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            if (!newUser.getLogin().equals(oldUser.getLogin())) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (!newUser.getEmail().equals(oldUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            log.info("Пользователь c id: {} обновлен", oldUser.getId());

            return oldUser;
        }
        log.error("User с id = {} не найден", newUser.getId());
        throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }
}
