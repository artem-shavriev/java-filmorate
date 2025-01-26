package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.*;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.*;
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
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {
        if (request.getMpa() != null) {
            int mpaId = request.getMpa().getId();

            Optional<Mpa> mpa = mpaStorage.findById(mpaId);
            if (mpa.isPresent()) {
                request.setMpa(mpa.get());
            } else {
                log.error("У рейтинга несуществующий id {}", mpaId);
                throw new NotFoundException("У рейтинга несуществующий id");
            }
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (request.getGenres() != null) {
            List<Integer> requestGenresId = request.getGenres().stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Integer> uniqueGenresIdsList = new ArrayList<>(uniqueGenresIds);

            List<Genre> uniqueGenres = genreStorage.findGenresByIds(uniqueGenresIdsList);

            if (uniqueGenres.size() != uniqueGenresIdsList.size()) {
                log.error("У фильма с названием: жанр с несуществующим id");
                throw new NotFoundException("У одного из жанров фильма несуществующий id");
            }

            request.setGenres(uniqueGenres);
        }

        if (request.getDirectors() != null) {
            List<Integer> requestDirectorsIdList = request.getDirectors().stream().map(dir -> dir.getId()).toList();

            request.setDirectors(directorStorage.findDirectorsByIds(requestDirectorsIdList));
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

        if (film.getDirectors() != null) {
            List<Director> directorsList = film.getDirectors();

            for (Director director : directorsList) {
                FilmDirector filmDirector = new FilmDirector();
                filmDirector.setDirectorId(director.getId());
                filmDirector.setFilmId(film.getId());

                filmDirectorStorage.addFilmDirector(filmDirector);
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

        if (request.getMpa() != null) {
            int mpaId = request.getMpa().getId();

            Optional<Mpa> mpa = mpaStorage.findById(mpaId);
            if (mpa.isPresent()) {
                request.setMpa(mpa.get());
            } else {
                log.error("У рейтинга несуществующий id {}", mpaId);
                throw new ValidationException("У рейтинга несуществующий id");
            }
        }

        filmGenreStorage.deleteFilmGenreByFilmId(request.getId());

        if (request.getGenres() != null) {
            List<Integer> requestGenresId = request.getGenres().stream().map(genre -> genre.getId()).toList();
            Set<Integer> uniqueGenresIds = new HashSet<>(requestGenresId);
            List<Integer> uniqueGenresIdsList = new ArrayList<>(uniqueGenresIds);

            List<Genre> uniqueGenres = genreStorage.findGenresByIds(uniqueGenresIdsList);

            if (uniqueGenres.size() != uniqueGenresIdsList.size()) {
                log.error("У фильма с названием: жанр с несуществующим id");
                throw new ValidationException("У одного из жанров фильма несуществующий id");
            }

            request.setGenres(uniqueGenres);

            List<Genre> genres = request.getGenres();

            for (Genre genre : genres) {
                FilmGenre filmGenre = new FilmGenre();
                filmGenre.setFilmId(request.getId());
                filmGenre.setGenreId(genre.getId());
                filmGenreStorage.addGenre(filmGenre);
            }
        }

        if (request.getDirectors() != null) {
            List<Integer> requestDirectorsIdList = request.getDirectors().stream().map(dir -> dir.getId()).toList();

            request.setDirectors(directorStorage.findDirectorsByIds(requestDirectorsIdList));

            List<Director> directorsList = request.getDirectors();

            for (Director director : directorsList) {
                FilmDirector filmDirector = new FilmDirector();

                filmDirector.setDirectorId(director.getId());
                filmDirector.setFilmId(request.getId());

                filmDirectorStorage.addFilmDirector(filmDirector);
            }
        } else {
            filmDirectorStorage.deleteFilmDirectorByFilmId(request.getId());
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
        return filmDbStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Integer filmId) {
        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id {} не найден.", filmId);
            throw new NotFoundException("Фильм с данным id не найден.");
        }
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

        eventService.createEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);

        log.info("Пользовтель с id {} лайкнул фильм с id {}.", userId, filmId);

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
            log.error("Пользователь с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользователь еще не лайкал этот фильм.");
        }

        likesFromUsersStorage.deleteLike(filmId, userId);
        log.info("Лайк пользователя с id {} фильму с id {} был удален.", userId, filmId);

        eventService.createEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);

        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmDbStorage.findPopularFilms(count, genreId, year);

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getCommonFilms(Integer userId, Integer friendId) {
        Optional<User> existUser = userDbStorage.findById(userId);
        if (existUser.isEmpty()) {
            log.error("Переданный userId не существует.");
            throw new NotFoundException("userId не найден.");
        }

        Optional<User> existFriend = userDbStorage.findById(userId);
        if (existFriend.isEmpty()) {
            log.error("Переданный friendId не существует.");
            throw new NotFoundException("friendId не найден.");
        }

        List<LikesFromUsers> likesFromUsersList = likesFromUsersStorage.findLikesByUserId(userId);
        List<Integer> userFilmsIdList = likesFromUsersList.stream().map(x -> x.getFilmId()).toList();

        List<LikesFromUsers> likesFromFriendList = likesFromUsersStorage.findLikesByUserId(friendId);
        List<Integer> friendFilmsIdList = likesFromFriendList.stream().map(x -> x.getFilmId()).toList();

        List<Integer> commonFilmsId = new ArrayList<>();

        userFilmsIdList.stream().forEach(id -> {
            if (friendFilmsIdList.contains(id)) {
                commonFilmsId.add(id);
            }
        });

        List<FilmDto> commonFilms = new ArrayList<>();

        List<FilmDto> allFilms = getFilms();
        allFilms.forEach(film -> {
            if (commonFilmsId.contains(film.getId())) {
                commonFilms.add(film);
            }
        });

        return commonFilms;
    }

    public FilmDto deleteFilmById(Integer filmIdForDelete) {
        FilmDto filmForDelete = getFilmById(filmIdForDelete);

        filmGenreStorage.deleteFilmGenreByFilmId(filmIdForDelete);
        log.info("Удалены записи о жанрах удаляемого фильма");
        filmDirectorStorage.deleteFilmDirectorByFilmId(filmIdForDelete);
        log.info("Удалены записи о режиссерах удаляемого фильма");
        likesFromUsersStorage.deleteLikesFromUsersByFilmId(filmIdForDelete);
        log.info("Удалены записи о лайках удаляемого фильма");
        filmDbStorage.deleteFilmById(filmIdForDelete);
        log.info("Удален фильм с id: {}", filmIdForDelete);

        return filmForDelete;
    }

    public List<FilmDto> getFilmsByDirectorSortByYear(Integer directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            log.error("Директор с id {} не найден.", directorId);
            throw new NotFoundException("Директор с данным id отсутствует");
        }
        List<Film> findFilms = filmDbStorage.findByDirectorSortByYear(directorId);

        return findFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsByDirectorSortByLike(Integer directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            log.error("Директор с id {} не найден.", directorId);
            throw new NotFoundException("Директор с данным id отсутствует");
        }
        List<Film> findFilms = filmDbStorage.findByDirectorSortByLike(directorId);

        return findFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getRecommendations(Integer userId) {
        Integer newRecommendation = filmDbStorage.findUserWithSimilarLikes(userId);
        if (newRecommendation == null) {
            return new ArrayList<>();
        }

        List<Integer> currentUserFilmIds = filmDbStorage.getFilmIdsByUserId(userId);

        List<Film> recommendedFilms = filmDbStorage.getFilmsByUserIdExcude(newRecommendation, currentUserFilmIds);

        return recommendedFilms.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getSearch(String query, String by) {
        List<FilmDto> result;
        if (query == null || by == null) {
            result = filmDbStorage.findAll()
                    .stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
            return result;
        } else if (by.contains("director") || by.contains("title")) {
            return filmDbStorage.getSearch(query, by);
        } else {
            throw new InternalServerException("Произошла ошибка");
        }
    }

    private Map<Integer, List<Integer>> getAllUserLikes() {
        List<Integer> allUserIds = likesFromUsersStorage.getAllUserIds();

        Map<Integer, List<Integer>> userLikes = new HashMap<>();
        for (Integer userId : allUserIds) {
            userLikes.put(userId, likesFromUsersStorage.getLikedFilmsId(userId));
        }

        return userLikes;
    }
}

