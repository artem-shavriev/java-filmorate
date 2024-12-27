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
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.LikesFromUsersRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
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

class FilmDbTest {
    private final FilmDbStorage filmDbStorage;

    @BeforeEach
    public void beforeEach() {

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
    }

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = filmDbStorage.findById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindFilmByName() {

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

        Film film2 = new Film();
        film2.setName("TestFilm2");
        film2.setDescription("TestDescription2");
        film2.setReleaseDate(LocalDate.now());
        film2.setMpa(mpa);
        film2.setDuration(102);

        filmDbStorage.addFilm(film2);

        Optional<Film> filmOptional = Optional.of(filmDbStorage.findAll().get(0));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm")
                );
        Optional<Film> filmOptional2 = Optional.of(filmDbStorage.findAll().get(1));

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
        film.setName("TestFilm1");
        film.setDescription("TestDescription1");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);

        Optional<Film> filmOptional = filmDbStorage.findByName("TestFilm1");

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "TestFilm1")
                );
    }

    @Test
    public void testUpdateFilm() {

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
