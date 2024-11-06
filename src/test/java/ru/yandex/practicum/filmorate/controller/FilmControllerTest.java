package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void shouldNotAddFilmWithoutName() {
        Film testFilm = new Film();
        testFilm.setName("");
        testFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(130);

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm),
                "Должно выбрасываться исключение валидации из-за отсутствия названия.");

        Film testFilm2 = new Film();
        testFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(130);

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm2),
                "Должно выбрасываться исключение валидации из-за отсутствия названия.");
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

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm),
                "Должно выбрасываться исключение валидации из-за описания более 200 символов.");

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
                "Должно выбрасываться исключение валидации из-за даты выхода.");

        Film testFilm2 = new Film();
        testFilm2.setName("New film 2");
        testFilm2.setReleaseDate(LocalDate.of(1895, 12, 29));
        testFilm2.setDescription("Description of Test film");
        testFilm2.setDuration(130);
        filmController.addFilm(testFilm2);

        assertEquals(2, filmController.getFilms().size(),
                "Фильм не добавлен из за граничного значения даты выхода.");
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() {
        Film testFilm = new Film();
        testFilm.setName("Test film");
        testFilm.setReleaseDate(LocalDate.of(2024, 2, 2));
        testFilm.setDescription("Description of Test film");
        testFilm.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmController.addFilm(testFilm),
                "Должно выбрасываться исключение валидации из-за отрицательной длительности.");

        Film testFilm2 = new Film();
        testFilm2.setName("Test film2");
        testFilm2.setReleaseDate(LocalDate.of(2024, 2, 2));
        testFilm2.setDescription("Description of Test film");
        testFilm2.setDuration(1);
        filmController.addFilm(testFilm2);

        assertEquals(2, filmController.getFilms().size(),
                "Фильм не добавлен из за граничного значения длительности 1 минута.");
    }
}
