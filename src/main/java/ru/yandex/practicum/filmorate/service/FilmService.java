package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesFromUsersStorage likesFromUsersRepository;
    private final FilmGenreStorage filmGenreRepository;
    private static final Date MIN_RELEASE_DATE = new Date(-5, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }

        Optional<Film> alreadyExistFilm = filmDbStorage.findByName(request.getName());
        if (alreadyExistFilm.isPresent()) {
            throw new DuplicatedDataException("Фильм с таким названием уже есть в списке.");
        }

        if (request.getReleaseDate().getTime() < MIN_RELEASE_DATE.getTime()) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        Film film = FilmMapper.mapToFilm(request);

        film = filmDbStorage.save(film);
        filmGenreRepository.setFilmGenres(film);

        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(Long filmId, UpdateFilmRequest request) {
        if (filmId == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }

        Optional<Film> existFilm = filmDbStorage.findByName(request.getName());
        if (existFilm.isEmpty()) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (request.getName() != null) {
            Optional<Film> alreadyExistName = filmDbStorage.findByName(request.getName());
            if (alreadyExistName.isPresent()) {
                throw new DuplicatedDataException("Фильм с таким названием уже есть в списке.");
            }
        }

        if (request.getReleaseDate().before(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        Film updateFilm = filmDbStorage.findById(filmId)
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updateFilm = filmDbStorage.update(updateFilm);
        log.info("Фильм {} был обновлен.", updateFilm.getName());

        return FilmMapper.mapToFilmDto(updateFilm);
    }

    public List<FilmDto> getFilms() {
        log.info("Получен список фильмов.");
        return  filmDbStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(long filmId) {
        FilmDto film = FilmMapper.mapToFilmDto(filmDbStorage.findById(filmId).get());
        return film;
    }

    public FilmDto likeFilm(Long filmId, Long userId) {

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        likesFromUsersRepository.addLike(filmId, userId);
        log.info("Пользовтель с id {} лайкнул фильм с id {}.", userId, filmId);

        return getFilmById(filmId);
    }

    public FilmDto deleteLikeFromFilm(Long filmId, Long userId) {

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        Film film = filmDbStorage.findById(filmId).get();
        Set<Long> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользовтель с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользовтаель еще не лайкал этот фильм.");
        }

        likesFromUsersRepository.deleteLike(filmId, userId);
        log.info("Лайк пользовтеля с id {} фильму с id {} был удален.", userId, filmId);

        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> filmsList = filmDbStorage.findAll();

        List<FilmDto> sortedFilmsIdsByLikes = new ArrayList<>();
        List<FilmDto> listOfPopularFilms = new ArrayList<>();
        TreeMap<Integer, Long> sortedMapOfFilmsLikes = new TreeMap<>();

        for (Film film : filmsList) {
            sortedMapOfFilmsLikes.put(film.getLikesFromUsers().size(), film.getId());
        }

        for (Long id : sortedMapOfFilmsLikes.values()) {
            sortedFilmsIdsByLikes.add(getFilmById(id));
        }

        if (sortedFilmsIdsByLikes.size() <= count) {
            for (int i = sortedFilmsIdsByLikes.size() - 1; i >= 0; i--) {
                listOfPopularFilms.add(sortedFilmsIdsByLikes.get(i));
            }
        } else {
            for (int i = count - 1; i >= 0; i--) {
                listOfPopularFilms.add(sortedFilmsIdsByLikes.get(i));
            }
        }

        log.info("Список наиболее популярных фильмов сформирован. Длина списка: {}", count);
        return listOfPopularFilms;
    }

    public FilmDto addGenre(long filmId, long genreId) {
        filmGenreRepository.addGenre(filmId, genreId);

        return getFilmById(filmId);
    }

    public FilmDto deleteGenre(long filmId, long genreId) {
        filmGenreRepository.deleteGenre(filmId, genreId);

        return getFilmById(filmId);
    }
}

