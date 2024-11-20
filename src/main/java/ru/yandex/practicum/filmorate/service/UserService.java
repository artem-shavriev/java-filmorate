package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(Long userId, Long friendId) {
        HashMap<Long, User> users = (HashMap<Long, User>) inMemoryUserStorage.getUsers();

        if (!users.keySet().contains(userId)) {
            log.error("userId не найден.");
            throw new NotFoundException("userId не найден.");
        }
        if (!users.keySet().contains(friendId)) {
            log.error("friendId не найден.");
            throw new NotFoundException("friendId не найден.");
        }

        if (users.get(userId).getFriendsId().contains(friendId)) {
            log.error("Этот пользователь уже в друзьях.");
            throw new DuplicateException("Этот пользователь уже в друзьях.");
        }
        users.get(userId).getFriendsId().add(friendId);
        users.get(friendId).getFriendsId().add(userId);
        log.trace("Пользователи c id: {} и {} добавлены друг к другу в друзья.", friendId, userId);

        return users.get(friendId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        HashMap<Long, User> users = (HashMap<Long, User>) inMemoryUserStorage.getUsers();

        if (!users.keySet().contains(userId)) {
            log.error("userId не найден.");
            throw new NotFoundException("userId не найден.");
        }
        if (!users.keySet().contains(friendId)) {
            log.error("friendId не найден.");
            throw new NotFoundException("friendId не найден.");
        }

        if (!users.get(userId).getFriendsId().contains(friendId)) {
            log.error("Этого пользователя нет в друзьях.");
            throw new DuplicateException("Этого пользователя нет в друзьях.");
        }
        users.get(userId).getFriendsId().remove(friendId);
        users.get(friendId).getFriendsId().remove(userId);
        log.trace("Пользователи c id: {} и {} удалены из друзей у друг друга.", friendId, userId);

        return users.get(friendId);
    }


}

