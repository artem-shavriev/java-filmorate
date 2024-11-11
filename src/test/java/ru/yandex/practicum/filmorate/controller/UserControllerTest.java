package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    UserController userController = new UserController();

    @BeforeEach
    public void beforeEach() {
        cleanUsers();
        User user = new User();
        user.setName("New User");
        user.setEmail("@user1.com");
        user.setLogin("userLogin1");
        user.setBirthday(LocalDate.of(1990, 12, 21));
        userController.addUser(user);
    }

    public void cleanUsers() {
        userController.getUsers().clear();
    }

    @Test
    public void shouldGetUsers() {
        assertEquals(1, userController.getUsers().size(), "User не получен");
    }

    @Test
    public void shouldAddUser() {
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("@testUser1.com");
        testUser.setLogin("TestUserLogin");
        testUser.setBirthday(LocalDate.of(1990, 12, 21));
        userController.addUser(testUser);

        assertNotNull(testUser.getId(), "id не должен быть null");
        assertEquals(userController.getUsers().size(), 2, "Второй user не добавлен");
    }

    @Test
    public void shouldNotAddUserWithDuplicateLogin() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("@testUser1.com");
        user.setLogin("userLogin1");
        user.setBirthday(LocalDate.of(1990, 12, 21));

        assertThrows(ValidationException.class, () -> userController.addUser(user),
                "Исключение валидации из за дубликата логина не было выброшено");
    }

    @Test
    public void shouldNotAddUserWithDuplicateEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("@user1.com");
        user.setLogin("TestUserLogin");
        user.setBirthday(LocalDate.of(1990, 12, 21));

        assertThrows(ValidationException.class, () -> userController.addUser(user),
                "Исключение валидации из за дубликата email не было выброшено.");
    }

    @Test
    public void shouldNotAddUserWithIncorrectEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test.com");
        user.setLogin("TestUserLogin");
        user.setBirthday(LocalDate.of(1990, 12, 21));

        assertEquals(1, userController.getUsers().size(),
                "Пользователь без @ в email был добавлен.");
    }

    @Test
    public void shouldAddUserWithoutName() {
        User user2 = new User();
        user2.setEmail("@test2.com");
        user2.setLogin("TestUserLogin");
        user2.setBirthday(LocalDate.of(1990, 12, 21));
        userController.addUser(user2);

        assertEquals(2, userController.getUsers().size(), "User не был добавлен без имени.");
        assertEquals(user2.getLogin(), user2.getName(), "Логин не скопирован в поле имени.");
    }

    @Test
    public void shouldUpdateUser() {
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setName("Update User");
        updateUser.setEmail("@UpdateUser1.com");
        updateUser.setLogin("UpdateUserLogin");
        updateUser.setBirthday(LocalDate.of(1995, 10, 11));
        userController.updateUser(updateUser);

        assertEquals("Update User", updateUser.getName(), "Имя не обновлено.");
        assertEquals("@UpdateUser1.com", updateUser.getEmail(),
                "Email не обновлен.");
        assertEquals("UpdateUserLogin", updateUser.getLogin(), "Логин не обновлен.");
        assertEquals(LocalDate.of(1995, 10, 11), updateUser.getBirthday(),
                "Деньрождения не обновлен.");
    }

    @Test
    public void shouldNotUpdateUserWthNonExistentId() {
        User updateUser = new User();
        updateUser.setId(11L);
        updateUser.setName("Update User");
        updateUser.setEmail("@UpdateUser1.com");
        updateUser.setLogin("UpdateUserLogin");
        updateUser.setBirthday(LocalDate.of(1995, 10, 11));

        assertThrows(NotFoundException.class, () -> userController.updateUser(updateUser),
                "Исключение не было выброшено из-за несуществующего id.");
    }
}
