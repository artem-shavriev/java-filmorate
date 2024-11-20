package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;
}
