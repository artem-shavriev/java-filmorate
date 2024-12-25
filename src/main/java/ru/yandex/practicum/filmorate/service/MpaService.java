package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(long mpaId) {
        Optional<Mpa> existMpa = mpaStorage.findById(mpaId);
        if (existMpa.isEmpty()) {
            log.error("Рейтинг с id {} не найден", mpaId);
            throw new NotFoundException("id mpa не найден.");
        }
        log.trace("Получен рейтинг фильма по его id: {}", mpaId);

        return mpaStorage.findById(mpaId).get();
    }
}
