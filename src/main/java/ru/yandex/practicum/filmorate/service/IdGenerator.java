package ru.yandex.practicum.filmorate.service;

import java.util.Map;

public abstract class IdGenerator {

    protected long getNextId(Map data) {
        long currentMaxId = data.keySet()
                .stream()
                .mapToLong(id -> (long) id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
