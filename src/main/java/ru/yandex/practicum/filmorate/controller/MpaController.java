package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MpaController {
    MpaService mpaService;

    @GetMapping("/mpa")
    public List<Mpa> getGenres() {
        return mpaService.findAll();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable long id) {
        return mpaService.findById(id);
    }
}
