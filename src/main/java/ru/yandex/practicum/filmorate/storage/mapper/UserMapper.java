package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        User user = new User();

        user.setLogin(request.getLogin());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setPassword(request.getPassword());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setBirthday(user.getBirthday());
        userDto.setFriendsId(user.getFriendsId());

        return userDto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }

        if (request.hasName()) {
            user.setUsername(request.getUsername());
        }

        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }

        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }

        if (request.hasPassword()) {
            user.setPassword(request.getPassword());
        }

        return user;
    }


}
