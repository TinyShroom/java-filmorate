package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingMpaController {

    private final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public Mpa getById(@PathVariable Integer id) {
        log.info("GET /mpa: {}", id);
        var mpa = mpaService.getById(id);
        log.info("completion GET /mpa: {}", mpa);
        return mpa;
    }

    @GetMapping("/mpa")
    public List<Mpa> getAll() {
        log.info("GET /mpa: all");
        var result = mpaService.getAll();
        log.info("completion GET /mpa: size {}", result.size());
        return result;
    }
}
