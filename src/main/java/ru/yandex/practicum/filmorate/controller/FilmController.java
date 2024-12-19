package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/films")
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest request) {
        return filmService.addFilm(request);
    }

    @PutMapping("/films/{id}")
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest newFilm, @PathVariable long id) {
        return filmService.updateFilm(id, newFilm);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public FilmDto deleteLikeFromFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/films/{id}/genre/{genreId}")
    public FilmDto addGenre(@PathVariable long id, @PathVariable long genreId) {
        return filmService.addGenre(id, genreId);
    }

    @DeleteMapping("/films/{id}/genre/{genreId}")
    public FilmDto deleteGenre(@PathVariable long id, @PathVariable long genreId) {
        return filmService.deleteGenre(id,genreId);
    }



}
