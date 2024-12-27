package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
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

class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;


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

        User updatedUser = userStorage.findById(1).get();

        updatedUser.setName("updateName");

        userStorage.updateUser(updatedUser);

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

    @Test
    public void testFindFilmById() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);

        Optional<Film> filmOptional = filmDbStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

   @Test
   public void testFindFilmByName() {
       Mpa mpa = new Mpa();
       mpa.setId(1);
       mpa.setName("G");

       Film film = new Film();
       film.setName("TestFilm");
       film.setDescription("TestDescription");
       film.setReleaseDate(LocalDate.now());
       film.setMpa(mpa);
       film.setDuration(100);

       filmDbStorage.addFilm(film);

       Optional<Film> filmOptional = filmDbStorage.findByName("TestFilm");

       assertThat(filmOptional)
               .isPresent()
               .hasValueSatisfying(user ->
                       assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm")
               );
   }

    @Test
    public void testFindAllFilms() {
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

        Optional<Film> filmOptional = filmDbStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm")
                );
        Optional<Film> filmOptional2 = filmDbStorage.findById(2);

        assertThat(filmOptional2)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm2")
                );
    }

    @Test
    public void testAddFilm() {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);

        Optional<Film> filmOptional = filmDbStorage.findByName("TestFilm");

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm")
                );
    }

    @Test
    public void testUpdateFilm() {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);

        Film updatedFilm = filmDbStorage.findById(1).get();

        updatedFilm.setName("updateName");

        Optional<Film> newFilm = filmDbStorage.findById(1);
        assertThat(newFilm)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm")
                );
    }
}