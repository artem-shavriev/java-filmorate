package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendsIdsMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class,
        UserRowMapper.class,
        FriendsIdsStorage.class,
        FriendsIdsMapper.class,
})

class UserDbTest {
    private final UserDbStorage userStorage;


    @Test
    public void testFindUserById() {

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        userStorage.addUser(user);

        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindUserByEmail() {
        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        userStorage.addUser(user);

        Optional<User> userOptional = userStorage.findByEmail("testMail");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", "testMail")
                );
    }

    @Test
    public void testFindUserByLogin() {

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        userStorage.addUser(user);

        Optional<User> userOptional = userStorage.findByLogin("testLogin");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "testLogin")
                );
    }

    @Test
    public void testAddUser() {

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        userStorage.addUser(user);
        Optional<User> userOptional = userStorage.findByLogin("testLogin");

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "testLogin")
                );
    }

    @Test
    public void testUpdateUser() {

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        userStorage.addUser(user);

        User UpdatedUser = userStorage.findById(1).get();

        UpdatedUser.setName("updateName");

        userStorage.updateUser(UpdatedUser);

        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "updateName")
                );
    }

    @Test
    public void testFindAllUsers() {
        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        User user1 = new User();
        user1.setName("testName1");
        user1.setLogin("testLogin1");
        user1.setEmail("testMail1");

        userStorage.addUser(user);
        userStorage.addUser(user1);

        List<User> userList= userStorage.findAll();

        assertThat(Optional.of(userList.get(0)))
                .isNotNull()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "testLogin")
                );
        assertThat(Optional.of(userList.get(1)))
                .isNotNull()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "testLogin1")
                );
    }
}
