package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

public interface UserStorage {

    public User addUser(User user);

    public User updateUser(User newUser);

    Optional<User> findById(int id);
}
