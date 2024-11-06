package ru.yandex.practicum.filmorate.controller;

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

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    final int maxDescriptionLength = 200;
    final Instant minReleaseDate = Instant.parse("1985-12-28");


    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    public String filmValidator(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            return "Название фильма не может быть пустым.";
        }
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName())) {
                return "Фильм с таким названием уже есть в списке.";
            }
        }
        if (film.getDuration().toMinutes() <= 0) {
           return "Продолжительность фильма должна быть положительным числом.";
        }
        if (film.getDescription().length() > maxDescriptionLength) {
            return "Максимальная длина описания не должна превышать 200 символов.";
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            return "Дата релиза должна быть не раньше 28 декабря 1895 года";
        }
        return "true";
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (!filmValidator(film).equals("true")) {
            log.error(filmValidator(film));
            throw new ValidationException(filmValidator(film));
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новыйфильм {}", film.getName());

    return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            if (!filmValidator(newFilm).equals("true")) {
                log.error(filmValidator(newFilm));
                throw new ValidationException(filmValidator(newFilm));
            }
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм был обновлен.");
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
