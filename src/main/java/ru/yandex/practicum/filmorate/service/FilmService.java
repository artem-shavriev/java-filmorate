package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmRepository;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersRepository;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikesFromUsersRepository likesFromUsersRepository;
    private final FilmGenreRepository filmGenreRepository;

    public FilmDto likeFilm(Long filmId, Long userId) {

        if (filmRepository.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        likesFromUsersRepository.addLike(filmId, userId);
        log.info("Пользовтель с id {} лайкнул фильм с id {}.", userId, filmId);

        Optional<Film> film = filmRepository.findById(filmId);

        return FilmMapper.mapToFilmDto(film.get());
    }

    public FilmDto deleteLikeFromFilm(Long filmId, Long userId) {

        if (filmRepository.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        Film film = filmRepository.findById(filmId).get();
        Set<Long> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользовтель с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользовтаель еще не лайкал этот фильм.");
        }

        likesFromUsersRepository.deleteLike(filmId, userId);
        log.info("Лайк пользовтеля с id {} фильму с id {} был удален.", userId, filmId);

        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId).get());
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> filmsList = filmRepository.findAll();

        List<Film> sortedFilmsIdsByLikes = new ArrayList<>();
        List<FilmDto> listOfPopularFilms = new ArrayList<>();
        TreeMap<Integer, Long> sortedMapOfFilmsLikes = new TreeMap<>();

        for (Film film : filmsList) {
            sortedMapOfFilmsLikes.put(film.getLikesFromUsers().size(), film.getId());
        }

        for (Long id : sortedMapOfFilmsLikes.values()) {
            sortedFilmsIdsByLikes.add(filmRepository.findById(id).get());
        }

        if (sortedFilmsIdsByLikes.size() <= count) {
            for (int i = sortedFilmsIdsByLikes.size() - 1; i >= 0; i--) {
                listOfPopularFilms.add(FilmMapper.mapToFilmDto(sortedFilmsIdsByLikes.get(i)));
            }
        } else {
            for (int i = count - 1; i >= 0; i--) {
                listOfPopularFilms.add(FilmMapper.mapToFilmDto(sortedFilmsIdsByLikes.get(i)));
            }
        }

        log.info("Список наиболее популярных фильмов сформирован. Длина списка: {}", count);
        return listOfPopularFilms;
    }

    public FilmDto addGenre(long filmId, long genreId) {
        filmGenreRepository.addGenre(filmId, genreId);

        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId).get());
    }

    public FilmDto deleteGenre(long filmId, long genreId) {
        filmGenreRepository.deleteGenre(filmId, genreId);

        return FilmMapper.mapToFilmDto(filmRepository.findById(filmId).get());
    }

    public List<Film> getFilms() {
        return filmDbStorage.getFilms();
    }

    public FilmDto addFilm(NewFilmRequest request) {
        return filmDbStorage.addFilm(request);
    }

    public FilmDto updateFilm(long id, UpdateFilmRequest request) {
        return filmDbStorage.updateFilm(id, request);
    }

}

