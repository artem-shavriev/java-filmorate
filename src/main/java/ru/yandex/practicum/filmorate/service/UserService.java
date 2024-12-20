package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsRepository;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserDbStorage userDbStorage;
    private final UserRepository userRepository;
    private final FriendsIdsRepository friendsIdsRepository;

    public UserDto addUser(NewUserRequest request) {
        return userDbStorage.addUser(request);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        return userDbStorage.updateUser(userId, request);
    }

    public List<UserDto> getUsers() {
        return userDbStorage.getUsers();
    }

    public UserDto addFriend(Long userId, Long friendId) {
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userRepository.findById(friendId);
        if (existFriend.isEmpty()) {
            throw new NotFoundException("friendId не найден.");
        }

        friendsIdsRepository.addFriend(friendId, userId);
        log.trace("Пользователь c id: {} добавил в друзья пользователя с id: {}", userId, friendId);

        return userDbStorage.getUserById(userId);
    }

    public UserDto deleteFriend(Long userId, Long friendId) {
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userRepository.findById(friendId);
        if (existFriend.isEmpty()) {
            log.error("Переданный friendId не существует.");
            throw new NotFoundException("friendId не найден.");
        }

        List<FriendsIds> friends = friendsIdsRepository.findUserFriends(userId);
        List<Long> usersFriendIds = new ArrayList<>();

        friends.stream()
                        .forEach(friend -> {
                            usersFriendIds.add(friend.getFriendId());
                        });

        if (!usersFriendIds.contains(friendId)) {
            log.trace("Данного пользователя небыло в друзьях.");
        } else {
            friendsIdsRepository.deleteLFriend(friendId, userId);
        }

        log.trace("Пользователь id: {} удалены из друзей у пользователя с id: {}", friendId, userId);

        return userDbStorage.getUserById(userId);
    }

    public List<UserDto> getFriends(Long userId) {
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        log.info("Список друзей пользователя с id {} сформирован.", userId);
        return userDbStorage.getFriends(userId);
    }

    public List<UserDto> commonFriends(Long userId, Long otherId) {
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existOtherUser= userRepository.findById(otherId);
        if (existOtherUser.isEmpty()) {
            log.error("Переданный otherId не существует.");
            throw new NotFoundException("otherId не найден.");
        }

        List<UserDto> userfriendsList = userDbStorage.getFriends(userId);
        List<UserDto> otherUserfriendsList = userDbStorage.getFriends(otherId);

        userfriendsList.retainAll(otherUserfriendsList);

        log.info("Список общих друзей пользователей с id: {} и {} сформирован.", userId, otherId);

        return userfriendsList;
    }

}

