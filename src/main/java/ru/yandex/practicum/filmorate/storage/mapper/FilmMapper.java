package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());
        film.setReleaseDate(request.getReleaseDate());
        film.setGenres(request.getGenres());
        film.setDirectors(request.getDirectors());

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();

        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setLikesFromUsers(film.getLikesFromUsers());
        filmDto.setGenres(film.getGenres());
        filmDto.setDirectors(film.getDirectors());

        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {

        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }

        if (request.hasName()) {
            film.setName(request.getName());
        }

        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }

        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }

        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }

        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }

        if (request.hasLikesFromUsers()) {
            film.setLikesFromUsers(request.getLikesFromUsers());
        }

        if (request.hasDirector()) {
            film.setDirectors(request.getDirectors());
        }

        return film;
    }
}
