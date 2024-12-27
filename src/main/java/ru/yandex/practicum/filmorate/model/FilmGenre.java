package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmGenre {
    private Integer id;
    private Integer filmId;
    private Integer genreId; //сделать int genreId

}
