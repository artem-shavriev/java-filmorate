package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(long genreId) {
        Optional<Genre> existGenre = genreStorage.findById(genreId);

        if(existGenre.isEmpty()) {
            log.error("Жанр с id {} не найден", genreId);
            throw new NotFoundException("Жанр с данным id не найден");
        }

        return genreStorage.findById(genreId).get();
    }
}
