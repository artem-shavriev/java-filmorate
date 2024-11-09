package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import java.util.Map;

public abstract class IdGenerator {

    protected long getNextId(@Valid Map data) {
        long currentMaxId = data.keySet()
                .stream()
                .mapToLong(id -> (long) id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
