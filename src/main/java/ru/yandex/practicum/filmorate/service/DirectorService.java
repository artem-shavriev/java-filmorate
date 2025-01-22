package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmDirectorStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findById(Integer directorId) {
        Optional<Director> existDirector = directorStorage.findById(directorId);

        if (existDirector.isEmpty()) {
            log.error("Режиссера с id {} нет.", directorId);
            throw new NotFoundException("Режиссер с данным id не найден");
        }

        return directorStorage.findById(directorId).get();
    }

    public Director addDirector(Director director) {
        Director newDirector = directorStorage.addDirector(director);
        log.info("Добавлен новый режиссер {} с id: {}", newDirector.getName(), newDirector.getId());
        return newDirector;
    }

    public Director updateDirector(Director directorForUpdate) {
        Integer directorId = directorForUpdate.getId();

        Optional<Director> existDirector = directorStorage.findById(directorId);

        if (existDirector.isEmpty()) {
            log.error("Режиссер с id {} не найден.", directorId);
            throw new NotFoundException("Режиссер с id: " + directorId + " не найден.");
        }

        Director updatedDirector = directorStorage.updateDirector(directorForUpdate);

        return updatedDirector;
    }

    public Director deleteDirector(Integer directorId) {
        Optional<Director> existDirector = directorStorage.findById(directorId);

        if (existDirector.isEmpty()) {
            log.error("Режиссер с id {} не найден.", directorId);
            throw new NotFoundException("Режиссер с id: " + directorId + " не найден.");
        }

        Director director = findById(directorId);

        directorStorage.deleteDirector(directorId);

        return director;
    }
}
