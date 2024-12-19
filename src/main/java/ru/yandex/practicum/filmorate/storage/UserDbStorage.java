package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage {
    public User addUser(User user) {

    }

    public User updateUser(User newUser) {

    }
}
