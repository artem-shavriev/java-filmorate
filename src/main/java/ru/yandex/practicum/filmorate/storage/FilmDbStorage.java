package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.FilmRepository;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage {
    FilmRepository filmRepository;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }

        Optional<Film> alreadyExistFilm = filmRepository.findByName(request.getName());
        if (alreadyExistFilm.isPresent()) {
            throw new DuplicatedDataException("Фильм с таким названием уже есть в списке.");
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        Film film = FilmMapper.mapToFilm(request);

        film = filmRepository.save(film);
        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(Long filmId, UpdateFilmRequest request) {
        if (filmId == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }

        Optional<Film> ExistFilm = filmRepository.findByName(request.getName());
        if (ExistFilm.isEmpty()) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        Film updateFilm = filmRepository.findById(filmId)
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updateFilm = filmRepository.update(updateFilm);

        log.info("Фильм {} был обновлен.", updateFilm.getName());

        return FilmMapper.mapToFilmDto(updateFilm);
    }

    public List<Film> getFilms() {
        log.info("Получен список фильмов.");
        return filmRepository.findAll();
    }

    public FilmDto getFilmById(long filmId) {
        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId).get());
    }
}
