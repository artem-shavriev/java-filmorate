package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.FriendsIdsStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendsIdsMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.LikesFromUsersRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class,
        UserRowMapper.class,
        FriendsIdsStorage.class,
        FriendsIdsMapper.class,
        FilmDbStorage.class,
        FilmRowMapper.class,
        FilmGenreStorage.class,
        FilmGenreRowMapper.class,
        GenreStorage.class,
        GenreRowMapper.class,
        LikesFromUsersStorage.class,
        LikesFromUsersRowMapper.class,
        MpaStorage.class,
        MpaRowMapper.class,
})
public class LikeFromUsersStorageTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikesFromUsersStorage likesFromUsersStorage;

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

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        Film film2 = new Film();
        film2.setName("TestFilm2");
        film2.setDescription("TestDescription2");
        film2.setReleaseDate(LocalDate.now());
        film2.setMpa(mpa);
        film2.setDuration(102);

        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);

        userDbStorage.addUser(user);
        userDbStorage.addUser(user2);
    }

    @Test
    public void addLikeTest() {
        int filmId = filmDbStorage.findAll().get(0).getId();
        int userId = userDbStorage.findAll().get(0).getId();

        likesFromUsersStorage.addLike(filmId,userId);

        int likesListSize = likesFromUsersStorage.findLikesByFilmId(filmId).size();

        assertThat(likesListSize).isEqualTo(1);
    }

    @Test
    public void findAllLikeTest() {
        likesFromUsersStorage.addLike(1,1);
        likesFromUsersStorage.addLike(1,2);

        int likesListSize = likesFromUsersStorage.findAll().size();

        assertThat(likesListSize).isEqualTo(2);
    }

    @Test
    public void findLikesByFilmIdTest() {
        int filmId = filmDbStorage.findAll().get(0).getId();
        int userId = userDbStorage.findAll().get(0).getId();

        likesFromUsersStorage.addLike(filmId,userId);

        int likesListSize = likesFromUsersStorage.findLikesByFilmId(filmId).size();

        assertThat(likesListSize).isEqualTo(1);
    }

    @Test
    public void deleteLikeTest() {

        int filmId = filmDbStorage.findAll().get(0).getId();
        int userId = userDbStorage.findAll().get(0).getId();
        int userId2 = userDbStorage.findAll().get(1).getId();

        likesFromUsersStorage.addLike(filmId,userId);
        likesFromUsersStorage.addLike(filmId,userId2);

        likesFromUsersStorage.deleteLike(filmId,userId2);

        int likesListSize = likesFromUsersStorage.findAll().size();

        assertThat(likesListSize).isEqualTo(1);
    }
}
