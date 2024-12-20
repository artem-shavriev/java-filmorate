package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.IdGenerator;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.storage.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController extends IdGenerator {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto addUser(@Valid @RequestBody NewUserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("/users/{id}")
    public UserDto updateUser(@PathVariable long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public UserDto deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<UserDto> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    /*@GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.commonFriends(id, otherId);
    }*/
}
