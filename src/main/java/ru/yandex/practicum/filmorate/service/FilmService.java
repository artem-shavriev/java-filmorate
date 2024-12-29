package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
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
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {

        if (request.getMpa().getId() > 5 || request.getMpa().getId() < 1) {
            throw new ValidationException("У рейтинга id от 1 до 5");
        }

        String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();

        request.getMpa().setName(mpaName);

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getGenres() != null) {
            request.getGenres().stream().forEach(genre -> {
                if (genre.getId() > 6 || genre.getId() < 1) {
                    throw new ValidationException("У жанров id от 1 до 6");
                }
            });

            List<Genre> genresList = request.getGenres();
            List<Integer> genresIds = genresList.stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(genresIds);
            List<Genre> uniqueGenresList = uniqueGenresIds.stream().map(id -> genreStorage.findById(id).get()).toList();

            for (Genre genre : uniqueGenresList) {
                String genreName = genreStorage.findById(genre.getId()).get().getName();
                genre.setName(genreName);
            }
            request.setGenres(uniqueGenresList);
        }

        Film film = FilmMapper.mapToFilm(request);

        film = filmDbStorage.addFilm(film);

        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                FilmGenre filmGenre = new FilmGenre();

                filmGenre.setFilmId(film.getId());
                filmGenre.setGenreId(genre.getId());

                filmGenreStorage.addGenre(filmGenre);
            }
        }

        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

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

        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        Film film = filmDbStorage.findById(filmId).get();
        Set<Integer> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользовтель с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользовтаель еще не лайкал этот фильм.");
        }

        likesFromUsersStorage.deleteLike(filmId, userId);
        log.info("Лайк пользовтеля с id {} фильму с id {} был удален.", userId, filmId);

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

