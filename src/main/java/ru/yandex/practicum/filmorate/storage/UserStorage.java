package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    public User addUser(User user);

    public User updateUser(User newUser);
}
