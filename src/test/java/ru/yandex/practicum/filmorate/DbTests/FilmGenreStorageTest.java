
package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
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
import java.util.List;

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
public class FilmGenreStorageTest {
    private final FilmGenreStorage filmGenreStorage;
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

        /*FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(1);
        filmGenre.setGenreId(1);
        filmGenreStorage.addGenre(filmGenre);*/
    }


    @Test
    public void testAddFilmGenre() {

        FilmGenre filmGenre = new FilmGenre();
        int id = filmDbStorage.findAll().get(0).getId();
        filmGenre.setFilmId(id);
        filmGenre.setGenreId(2);

        filmGenreStorage.addGenre(filmGenre);

        List<FilmGenre> filmGenres = filmGenreStorage.findAll();

        assertThat(filmGenres.size()).isEqualTo(1);
    }

    @Test
    public void testFindAllFilmGenre() {

        FilmGenre filmGenre = new FilmGenre();
        int id = filmDbStorage.findAll().get(0).getId();

        filmGenre.setFilmId(id);
        filmGenre.setGenreId(2);

        filmGenreStorage.addGenre(filmGenre);

        List<FilmGenre> filmGenres = filmGenreStorage.findAll();

        assertThat(filmGenres.size()).isEqualTo(1);
    }

    @Test
    public void testFindGenresByFilmId() {
        FilmGenre filmGenre = new FilmGenre();

        int id = filmDbStorage.findAll().get(0).getId();

        filmGenre.setFilmId(id);
        filmGenre.setGenreId(2);

        filmGenreStorage.addGenre(filmGenre);

        List<FilmGenre> findGenresList = filmGenreStorage.findGenresByFilmId(id);

        assertThat(findGenresList.size()).isEqualTo(1);
    }
}

