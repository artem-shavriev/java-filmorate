package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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
    private final LikesFromUsersStorage likesFromUsersStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final EventService eventService;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {

        if (request.getMpa() != null) {
            List<Mpa> mpaList = mpaStorage.findAll();
            List<Integer> mpaIdsList = mpaList.stream().map(Mpa::getId).toList();

            if (!mpaIdsList.contains(request.getMpa().getId())) {
                throw new ValidationException("У рейтинга несуществующий id");
            }

            String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();
            request.getMpa().setName(mpaName);
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getGenres() != null) {
            List<Genre> genresList = genreStorage.findAll();
            List<Integer> genresIdList = genresList.stream().map(Genre::getId).toList();

            request.getGenres().forEach(genre -> {
                if (!genresIdList.contains(genre.getId())) {
                    log.error("У фильма с названием: {} жанр с несуществующим id: {}", request.getName(), genre.getId());
                    throw new ValidationException("У одного из жанров фильма несуществующий id");
                }
            });

            List<Integer> requestGenresId = request.getGenres().stream().map(Genre::getId).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Genre> uniqueGenresList = uniqueGenresIds.stream()
                    .map(id -> genreStorage.findById(id).get())
                    .toList();

            for (Genre genre : uniqueGenresList) {
                String genreName = genreStorage.findById(genre.getId()).get().getName();
                genre.setName(genreName);
            }
            request.setGenres(uniqueGenresList);
        }

        // Маппинг DTO на модель
        Film film = FilmMapper.mapToFilm(request);

        // Добавление фильма в хранилище
        film = filmDbStorage.addFilm(film);

        // Добавление жанров, если они есть
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                FilmGenre filmGenre = new FilmGenre();
                filmGenre.setFilmId(film.getId());
                filmGenre.setGenreId(genre.getId());
                filmGenreStorage.addGenre(filmGenre);
            }
        }

        // Логгирование информации о новом фильме
        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        // Добавление события через EventService
        eventService.createEvent(film.getId(), EventType.LIKE, EventOperation.ADD, film.getId());

        // Возвращение DTO
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {

        Optional<Film> existFilm = filmDbStorage.findById(request.getId());
        if (existFilm.isEmpty()) {
            log.error("Фильм с id = {} не найден", request.getId());
            throw new NotFoundException("Фильм с id = " + request.getId() + " не найден");
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getMpa() != null) {
            List<Mpa> mpaList = mpaStorage.findAll();
            List<Integer> mpaIdsList = mpaList.stream().map(mpa -> mpa.getId()).toList();

            if (!mpaIdsList.contains(request.getMpa().getId())) {
                throw new ValidationException("У рейтинга несуществующий id");
            }

            String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();

            request.getMpa().setName(mpaName);
        }

        if (request.getGenres() != null) {

            List<Genre> genresList = genreStorage.findAll();
            List<Integer> genresIdList = genresList.stream().map(Genre::getId).toList();

            request.getGenres().stream().forEach(genre -> {
                if (!genresIdList.contains(genre.getId())) {
                    log.error("У фильма с названием: {} жанр с несуществующим id: {}", request.getName(), genre.getId());
                    throw new ValidationException("У одного из жанров фильма несуществующий id");
                }
            });

            List<Integer> requestGenresId = request.getGenres().stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Genre> uniqueGenresList = uniqueGenresIds.stream().map(id -> genreStorage.findById(id).get()).toList();

            for (Genre genre : uniqueGenresList) {
                String genreName = genreStorage.findById(genre.getId()).get().getName();
                genre.setName(genreName);
            }
            request.setGenres(uniqueGenresList);
        }

        Film updateFilm = filmDbStorage.findById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        updateFilm = filmDbStorage.updateFilm(updateFilm);
        log.info("Фильм {} был обновлен.", updateFilm.getName());

        return FilmMapper.mapToFilmDto(updateFilm);
    }

    public List<FilmDto> getFilms() {
        log.info("Получен список фильмов.");
        return  filmDbStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Integer filmId) {
        FilmDto film = FilmMapper.mapToFilmDto(filmDbStorage.findById(filmId).get());
        return film;
    }

    public FilmDto likeFilm(Integer filmId, Integer userId) {

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        LikesFromUsers like = likesFromUsersStorage.addLike(filmId, userId);
        getFilmById(filmId).getLikesFromUsers().add(like.getUserId());

        log.info("Пользовтель с idt {} лайкнул фильм с id {}.", userId, filmId);

        return getFilmById(filmId);
    }

    public FilmDto deleteLikeFromFilm(Integer filmId, Integer userId) {

        // Проверяем существование фильма
        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        // Проверяем существование пользователя
        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользователь с id: {} не существует.", userId);
            throw new NotFoundException("Пользователь с данным id не существует.");
        }

        // Получаем фильм и проверяем наличие лайка от пользователя
        Film film = filmDbStorage.findById(filmId).get();
        Set<Integer> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользователь с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользователь еще не лайкал этот фильм.");
        }

        // Удаляем лайк из хранилища
        likesFromUsersStorage.deleteLike(filmId, userId);
        log.info("Лайк пользователя с id {} фильму с id {} был удален.", userId, filmId);

        // Добавляем событие в ленту событий
        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);

        // Возвращаем обновленную информацию о фильме
        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> filmsList = filmDbStorage.findAll();

        List<FilmDto> sortedFilmsIdsByLikes = new ArrayList<>();
        List<FilmDto> listOfPopularFilms = new ArrayList<>();
        TreeMap<Integer, Integer> sortedMapOfFilmsLikes = new TreeMap<>();

        for (Film film : filmsList) {
            sortedMapOfFilmsLikes.put(film.getLikesFromUsers().size(), film.getId());
        }

        for (Integer id : sortedMapOfFilmsLikes.values()) {
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
}

