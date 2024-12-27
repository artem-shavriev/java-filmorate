package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FriendsIds;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendsIdsMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class,
        UserRowMapper.class,
        FriendsIdsStorage.class,
        FriendsIdsMapper.class,
})

public class FriendsIdsStorageTest {
    private final FriendsIdsStorage friendsIdsStorage;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    public void beforeEach() {

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testMail");

        User user2 = new User();
        user2.setName("testName2");
        user2.setLogin("testLogin2");
        user2.setEmail("testMail2");

        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
    }

    @Test
    public void addFriendTest() {
        friendsIdsStorage.addFriend(1,2);

        FriendsIds friendsIds = friendsIdsStorage.findUserFriends(1).get(0);

        assertThat(friendsIds).isNotNull();
    }

    @Test
    public void findAllFriendsIdsTest() {
        friendsIdsStorage.addFriend(1,2);
        friendsIdsStorage.addFriend(2,1);

        FriendsIds friendIds1 = friendsIdsStorage.findAll().get(0);
        FriendsIds friendIds2 = friendsIdsStorage.findAll().get(1);
        int friendIdListSize = friendsIdsStorage.findAll().size();


        assertThat(friendIds2).isNotNull();
        assertThat(friendIds1).isNotNull();
        assertThat(friendIdListSize).isEqualTo(2);
    }

    @Test
    public void deleteFriendTest() {
        friendsIdsStorage.addFriend(1,2);
        friendsIdsStorage.addFriend(2,1);
        friendsIdsStorage.deleteLFriend(2,1);

        int friendIdsSize = friendsIdsStorage.findAll().size();

        assertThat(friendIdsSize).isEqualTo(1);
    }
}
