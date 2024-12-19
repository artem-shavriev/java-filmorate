package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private long id;
    private long filmId;
    private long genreId; //сделать int genreId

}
