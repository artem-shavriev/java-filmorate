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
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<FilmDto> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/films")
    public FilmDto addFilm(@Valid @RequestBody NewFilmRequest request) {
        return filmService.addFilm(request);
    }

    @PutMapping("/films")
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public FilmDto deleteLikeFromFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(required = false) Integer count,
                                         @RequestParam(required = false, name = "genreId") Integer genreId,
                                         @RequestParam(required = false, name = "year") Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/films/director/{directorId}")
    public List<FilmDto> getDirectorFilmsSortedByYearOrLikes(@RequestParam String sortBy,
                                                             @PathVariable Integer directorId) {
        List<FilmDto> filmsList = filmService.getFilmsByDirector(directorId);
        if (sortBy.equals("year")) {
            filmsList = filmService.sortedByYear(filmsList);
        }

        if (sortBy.equals("likes")) {
            filmsList = filmService.sortedByLikes(filmsList);
        }

        return filmsList;
    }

    @GetMapping("/films/common")
    public List<FilmDto> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/search")
    public List<FilmDto> getSearch(@RequestParam(required = false, name = "query") String query,
                                   @RequestParam(required = false, name = "by") String by) {
        return filmService.getSearch(query, by);
    }

    @DeleteMapping("/films/{filmId}")
    public FilmDto deleteFilmById(@PathVariable Integer filmId) {
        return filmService.deleteFilmById(filmId);
    }
}