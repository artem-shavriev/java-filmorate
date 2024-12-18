package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    public Film addFilm(Film film){

    }

    public Film updateFilm(Film newFilm) {

    }
}
