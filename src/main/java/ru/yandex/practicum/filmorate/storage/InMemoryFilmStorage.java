
package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage extends IdGenerator implements FilmStorage {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> filmsMap = new HashMap<>();

    public HashMap<Long, Film> getFilmsMap() {
        return filmsMap;
    }

    public Collection<Film> getFilms() {
        log.info("Получен список фильмов.");
        return filmsMap.values();
    }

    public Film addFilm(Film film) {
        for (Film f : filmsMap.values()) {
            if (f.getName().equals(film.getName())) {
                throw new ValidationException("Фильм с таким названием уже есть в списке.");
            }
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getId() == null) {
            film.setId(getNextId(filmsMap));
        }
        filmsMap.put(film.getId(), film);
        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        return film;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }
        if (filmsMap.containsKey(newFilm.getId())) {
            for (Film f : filmsMap.values()) {
                if (f.getName().equals(newFilm.getName())) {
                    throw new ValidationException("Фильм с таким названием уже есть в списке.");
                }
            }
            if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
            }
            Film oldFilm = filmsMap.get(newFilm.getId());
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

