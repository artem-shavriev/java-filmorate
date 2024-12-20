package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(Long userId, Long friendId) {
        HashMap<Long, User> users = inMemoryUserStorage.getUsersMap();
        if (!users.containsKey(userId)) {
            log.error("userId не найден.");
            throw new NotFoundException("userId не найден.");
        }
        if (!users.containsKey(friendId)) {
            log.error("friendId не найден.");
            throw new NotFoundException("friendId не найден.");
        }

        users.get(userId).getFriendsId().add(friendId);
        users.get(friendId).getFriendsId().add(userId);
        log.trace("Пользователи c id: {} и {} добавлены друг к другу в друзья.", friendId, userId);

        return users.get(friendId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        HashMap<Long, User> users = inMemoryUserStorage.getUsersMap();

        if (!users.containsKey(userId)) {
            log.error("userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        if (!users.containsKey(friendId)) {
            log.error("friendId не существует.");
            throw new NotFoundException("friendId не найден.");
        }

        if (!users.get(userId).getFriendsId().contains(friendId)) {
            return users.get(friendId);
        }
        users.get(userId).getFriendsId().remove(friendId);
        users.get(friendId).getFriendsId().remove(userId);
        log.trace("Пользователи c id: {} и {} удалены из друзей у друг друга.", friendId, userId);

        return users.get(friendId);
    }

    public List<User> getFriends(Long userId) {
        HashMap<Long, User> users = inMemoryUserStorage.getUsersMap();

        if (!users.containsKey(userId)) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        List<User> friendsList = new ArrayList<>();

        if (users.get(userId).getFriendsId() == null) {
            log.error("У пользовтеля с id: {} пустой список друзей", userId);
            throw new NotFoundException("У пользовтеля пустой список друзей.");
        }

        Set<Long> friendsId = users.get(userId).getFriendsId();

        for (Long id: friendsId) {
            friendsList.add(users.get(id));
        }
        log.info("Список друзей пользователя с id {} сформирован.", userId);
        return friendsList;
    }

    public List<User> commonFriends(Long userId, Long otherId) {
        HashMap<Long, User> users = inMemoryUserStorage.getUsersMap();

        if (!users.containsKey(userId)) {
            log.error("Данного userId не существует.");
            throw new NotFoundException("userId не найден.");
        }
        if (!users.containsKey(otherId)) {
            log.error("Данного otherId не существует.");
            throw new NotFoundException("friendId не найден.");
        }

        List<User> userfriendsList = getFriends(userId);
        List<User> otherUserfriendsList = getFriends(otherId);
        userfriendsList.retainAll(otherUserfriendsList);

        log.info("Список общих друзей пользователей с id: {} и {} сформирован.", userId, otherId);
        return userfriendsList;
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return inMemoryUserStorage.updateUser(newUser);
    }

    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }


}

