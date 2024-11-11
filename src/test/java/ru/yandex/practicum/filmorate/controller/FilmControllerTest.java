package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    FilmController filmController = new FilmController() {
    };

    @BeforeEach
    public void beforeEach() {
        cleanFilms();
        Film film = new Film();
        film.setName("New film");
        film.setReleaseDate(LocalDate.of(2024, 9, 9));
        film.setDescription("Description of new film");
        film.setDuration(120);
        filmController.addFilm(film);
    }

    public void cleanFilms() {
        filmController.getFilms().clear();
    }

    @Test
    public void shouldGetFilms() {
        assertEquals(1, filmController.getFilms().size(), "Фильм не получен");
    }

    @Test
    public void shouldAddFilm() {
        Film testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(130);
        filmController.addFilm(testFilm);

        assertNotNull(testFilm.getId(), "id не должен быть null");
        assertEquals(filmController.getFilms().size(), 2, "Второй фильм не добавлен");
    }

    @Test
    public void shouldNotAddFilmWithDuplicateName() {
        Film testFilm = new Film();
        testFilm.setName("New film");
        testFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(130);

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm),
                "Должно выбрасываться исключение валидации из-за дублирования названия.");
    }

    @Test
    public void shouldNotAddFilmWithDescriptionSizeMoreThanMax() {
        Film testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm.setDescription("Description of Test film Description of Test film Description of Test film " +
                "Description of Test film Description of Test film Description of Test film " +
                "Description of Test film Description of Test film Description of Test film");
        testFilm.setDuration(130);

        assertEquals(1, filmController.getFilms().size(),
                "Фильм с описанием более 200 символов был добавлен.");

        Film testFilm2 = new Film();
        testFilm2.setName("Test film2");
        testFilm2.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm2.setDescription("Description of Test film Description of Test film Description of Test film " +
                "Description of Test film Description of Test film Description of Test film " +
                "Description of Test film Description of Test film");
        testFilm2.setDuration(130);
        filmController.addFilm(testFilm2);

        assertEquals(2, filmController.getFilms().size(),
                "Фильм с описанием 199 симфолов не был добавлен.");
    }

    @Test
    public void shouldNotAddFilmWithIncorrectReleaseDate() {
        Film testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(130);

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm),
                "Исключение валидации из-за некорректной даты выхода не выброшено.");

        Film testFilm2 = new Film();
        testFilm2.setName("New film 2");
        testFilm2.setReleaseDate(LocalDate.of(1895, 12, 29));
        testFilm2.setDescription("Description of Test film");
        testFilm2.setDuration(130);
        filmController.addFilm(testFilm2);

        assertEquals(2, filmController.getFilms().size(),
                "Фильм не добавлен из-за граничного значения даты выхода.");
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() {
        Film testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.of(2024, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(-1);

        assertEquals(1, filmController.getFilms().size(),
                "Фильм был добавлен с отрицательной длительностью.");

        Film testFilm3 = new Film();
        testFilm3.setName("Test film");
        testFilm3.setReleaseDate(LocalDate.of(2024, 2, 2));
        testFilm3.setDescription("Description of Test film");
        testFilm3.setDuration(0);

        assertEquals(1, filmController.getFilms().size(),
                "Фильм был добавлен с длительностью 0 минут.");

        Film testFilm2 = new Film();
        testFilm2.setName("Test film2");
        testFilm2.setReleaseDate(LocalDate.of(2024, 2, 2));
        testFilm2.setDescription("Description of Test film");
        testFilm2.setDuration(1);
        filmController.addFilm(testFilm2);

        assertEquals(2, filmController.getFilms().size(),
                "Фильм не добавлен из-за граничного значения длительности 1 минута.");
    }

    @Test
    public void shouldUpdateFilm() {
        Film filmForUpdate = new Film();
        filmForUpdate.setId(1L);
        filmForUpdate.setName("UpdateNew film");
        filmForUpdate.setReleaseDate(LocalDate.of(2022, 9, 9));
        filmForUpdate.setDescription("Update Description of new film");
        filmForUpdate.setDuration(125);
        Film update = filmController.updateFilm(filmForUpdate);

        assertEquals("UpdateNew film", update.getName(), "Название фильма не обновлено.");
        assertEquals("Update Description of new film", update.getDescription(),
                "Описание фильма не обновлено.");
        assertEquals(125, update.getDuration(), "Длительность фильма не обновлена.");
        assertEquals(LocalDate.of(2022, 9, 9), update.getReleaseDate(),
                "Дата выхода фильма не обновлена.");
    }

    @Test
    public void shouldNotUpdateFilmWthNonExistentId() {
        Film filmForUpdate = new Film();
        filmForUpdate.setId(11L);
        filmForUpdate.setName("UpdateNew film");
        filmForUpdate.setReleaseDate(LocalDate.of(2024, 9, 9));
        filmForUpdate.setDescription("Description of new film");
        filmForUpdate.setDuration(120);

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(filmForUpdate),
                "Исключение из-за несуществующего id не было выброшено.");
    }
}
