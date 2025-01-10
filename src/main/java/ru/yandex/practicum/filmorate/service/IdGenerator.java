package ru.yandex.practicum.filmorate.service;

import java.util.Map;

public abstract class IdGenerator {

    protected int getNextId(Map data) {
        Integer currentMaxId = data.keySet()
                .stream()
                .mapToInt(id -> (Integer) id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
