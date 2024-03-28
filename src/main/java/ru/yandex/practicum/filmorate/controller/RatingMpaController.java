package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingMpaController {

    private final RatingMpaService ratingMpaService;

    @GetMapping("/mpa/{id}")
    public RatingMpa getById(@PathVariable Integer id) {
        log.info("GET /mpa: {}", id);
        var mpa = ratingMpaService.getById(id);
        log.info("completion GET /mpa: {}", mpa);
        return mpa;
    }

    @GetMapping("/mpa")
    public List<RatingMpa> getAll() {
        log.info("GET /mpa: all");
        var result = ratingMpaService.getAll();
        log.info("completion GET /mpa: size {}", result.size());
        return result;
    }
}
