package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FriendsIdsStorage friendsIdsRepository;

    public UserDto addUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }

        Optional<User> alreadyExistEmail = userDbStorage.findByEmail(request.getEmail());
        if (alreadyExistEmail.isPresent()) {
            throw new ValidationException("Электронная почта уже используется");
        }

        if (request.getLogin() == null || request.getLogin().isEmpty()) {
            throw new ConditionsNotMetException("Login должен быть указан");
        }

        Optional<User> alreadyExistLogin = userDbStorage.findByLogin(request.getLogin());
        if (alreadyExistLogin.isPresent()) {
            throw new ValidationException("Такой логин уже существует.");
        }

        User user = UserMapper.mapToUser(request);
        user = userDbStorage.save(user);

        log.info("Создан новый пользователь c id: {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        if (userId == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }

        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("User с id = {} не найден", userId);
            throw new NotFoundException("User с id = " + userId + " не найден");
        }

        if (request.getLogin() != null) {
            Optional<User> alreadyExistLogin = userDbStorage.findByLogin(request.getLogin());
            if (alreadyExistLogin.isPresent()) {
                throw new ValidationException("Такой логин уже существует.");
            }
        }

        if (request.getEmail() != null) {
            Optional<User> alreadyExistEmail = userDbStorage.findByEmail(request.getEmail());
            if (alreadyExistEmail.isPresent()) {
                throw new ValidationException("Электронная почта уже используется");
            }
        }

        User updateUser = userDbStorage.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updateUser = userDbStorage.update(updateUser);
        log.info("Пользователь c id: {} обновлен", userId);

        return UserMapper.mapToUserDto(updateUser);
    }

    public List<UserDto> getUsers() {
        log.info("Получен список пользователей.");
        return userDbStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userDbStorage.findById(userId).get());
    }

    public UserDto getUserByLogin(String login) {
        return UserMapper.mapToUserDto(userDbStorage.findByLogin(login).get());
    }

    public UserDto getUserByEmail(String email) {
        return UserMapper.mapToUserDto(userDbStorage.findByEmail(email).get());
    }

    public UserDto addFriend(Long userId, Long friendId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userDbStorage.findById(friendId);
        if (existFriend.isEmpty()) {
            throw new NotFoundException("friendId не найден.");
        }

        friendsIdsRepository.addFriend(friendId, userId);
        log.trace("Пользователь c id: {} добавил в друзья пользователя с id: {}", userId, friendId);

        return getUserById(userId);
    }

    public UserDto deleteFriend(Long userId, Long friendId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userDbStorage.findById(friendId);
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

        return getUserById(userId);
    }

    public List<UserDto> getFriends(long userId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        List<Long> friendsIds = new ArrayList<>();
        List<UserDto> friends = new ArrayList<>();
        List<FriendsIds> friendsIdsObjects = friendsIdsRepository.findUserFriends(userId);

        friendsIdsObjects.stream().forEach(friendObject -> friendsIds.add(friendObject.getFriendId()));

        friendsIds.stream().forEach(friendId -> friends.add(getUserById(friendId)));

        return friends;
    }

    public List<UserDto> commonFriends(Long userId, Long otherId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existOtherUser = userDbStorage.findById(otherId);
        if (existOtherUser.isEmpty()) {
            log.error("Переданный otherId не существует.");
            throw new NotFoundException("otherId не найден.");
        }

        List<UserDto> userfriendsList = getFriends(userId);
        List<UserDto> otherUserfriendsList = getFriends(otherId);

        userfriendsList.retainAll(otherUserfriendsList);

        log.info("Список общих друзей пользователей с id: {} и {} сформирован.", userId, otherId);

        return userfriendsList;
    }
}

