package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getDirectors() {
        return directorService.findAll();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable Integer id) {
        return directorService.findById(id);
    }

    @PostMapping("/directors")
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public Director deleteDirector(@PathVariable Integer id) {
        return directorService.deleteDirector(id);
    }
}
