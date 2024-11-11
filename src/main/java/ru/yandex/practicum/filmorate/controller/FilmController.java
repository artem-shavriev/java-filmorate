package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends IdGenerator {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен список фильмов.");
        return films.values();
    }

    private void filmValidator(Film film) {
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName())) {
                throw new ValidationException("Фильм с таким названием уже есть в списке.");
            }
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        filmValidator(film);
        if (film.getId() == null) {
            film.setId(getNextId(films));
        }
        films.put(film.getId(), film);
        log.info("Добавлен новыйфильм {} с id: {}", film.getName(), film.getId());

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            filmValidator(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getDescription() != null) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм {} был обновлен.", oldFilm.getName());
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
