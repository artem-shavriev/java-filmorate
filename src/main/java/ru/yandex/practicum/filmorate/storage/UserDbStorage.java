package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsRepository;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage {
    UserRepository userRepository;
    FriendsIdsRepository friendsIdsRepository;

    public List<UserDto> getUsers() {
        log.info("Получен список пользователей.");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).get());
    }

    public UserDto getUserByLogin(String login) {
        return UserMapper.mapToUserDto(userRepository.findByLogin(login).get());
    }

    public UserDto getUserByEmail(String email) {
        return UserMapper.mapToUserDto(userRepository.findByEmail(email).get());
    }

    public UserDto addUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }

        Optional<User> alreadyExistEmail = userRepository.findByEmail(request.getEmail());
        if (alreadyExistEmail.isPresent()) {
            throw new ValidationException("Электронная почта уже используется");
        }

        if (request.getLogin() == null || request.getLogin().isEmpty()) {
            throw new ConditionsNotMetException("Login должен быть указан");
        }

        Optional<User> alreadyExistLogin = userRepository.findByLogin(request.getLogin());
        if (alreadyExistLogin.isPresent()) {
            throw new ValidationException("Такой логин уже существует.");
        }

        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);

        log.info("Создан новый пользователь c id: {}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        if (userId == null) {
            log.error("Id не куказан.");
            throw new ValidationException("Id должен быть указан");
        }

        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isEmpty()) {
            log.error("User с id = {} не найден", userId);
            throw new NotFoundException("User с id = " + userId + " не найден");
        }

        if (request.getLogin() != null) {
            Optional<User> alreadyExistLogin = userRepository.findByLogin(request.getLogin());
            if (alreadyExistLogin.isPresent()) {
                throw new ValidationException("Такой логин уже существует.");
            }
        }

        if (request.getEmail() != null) {
            Optional<User> alreadyExistEmail = userRepository.findByEmail(request.getEmail());
            if (alreadyExistEmail.isPresent()) {
                throw new ValidationException("Электронная почта уже используется");
            }
        }

        User updateUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        updateUser = userRepository.update(updateUser);
        log.info("Пользователь c id: {} обновлен", userId);

        return UserMapper.mapToUserDto(updateUser);
    }

    public List<UserDto> getFriends(long userId) {
        List<Long> friendsIds = new ArrayList<>();
        List<UserDto> friends = new ArrayList<>();
        List<FriendsIds> friendsIdsObjects = friendsIdsRepository.findUserFriends(userId);

        friendsIdsObjects.stream().forEach(friendObject -> friendsIds.add(friendObject.getFriendId()));

        friendsIds.stream().forEach(friendId -> friends.add(getUserById(friendId)));

        return friends;
    }
}