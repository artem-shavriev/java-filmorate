package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    public Film addFilm(Film film);

    public Film updateFilm(Film newFilm);
}
