package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    public Film likeThisFilm(Long filmId, Long userId) {
        HashMap<Long, Film> films = (HashMap<Long, Film>) inMemoryFilmStorage.getFilms();
        HashMap<Long, User> users = (HashMap<Long, User>) inMemoryUserStorage.getUsers();

        if (!films.containsKey(filmId)) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (!users.containsKey(userId)) {
            log.error("Пользовтель с id: {} не сущетвует.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        if (films.get(filmId).getLikesFromUsers().contains(userId)) {
            log.error("Пользовтель с id: {} уже лайкал фильм с id: {}.", userId, filmId);
            throw new DuplicateException("Данный пользовтаель уже лайкал этот фильм.");
        }
        films.get(filmId).getLikesFromUsers().add(userId);
        log.info("Пользовтель с id {} лайкнул фильм с id {}.", userId, filmId);

        return films.get(filmId);
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        HashMap<Long, Film> films = (HashMap<Long, Film>) inMemoryFilmStorage.getFilms();
        HashMap<Long, User> users = (HashMap<Long, User>) inMemoryUserStorage.getUsers();

        if (!films.containsKey(filmId)) {
            log.error("Фильм с id: {} не найден.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (!users.containsKey(userId)) {
            log.error("Пользовтель с id: {} не найден.", userId);
            throw new NotFoundException("Пользовтель с данным id не сущетвует.");
        }

        if (!films.get(filmId).getLikesFromUsers().contains(userId)) {
            log.error("Пользовтель с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new DuplicateException("Данный пользовтаель еще не лайкал этот фильм.");
        }
        films.get(filmId).getLikesFromUsers().remove(userId);
        log.info("Лайк пользовтеля с id {} фильму с id {} был удален.", userId, filmId);

        return films.get(filmId);
    }

}
