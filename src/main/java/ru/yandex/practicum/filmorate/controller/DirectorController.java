package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director add(@Valid @RequestBody Director director) {
        log.info("POST /directors: {}", director.toString());
        var result = directorService.add(director);
        log.info("completion POST /directors: {}", result);
        return result;
    }

    @PutMapping
    public Director change(@Valid @RequestBody Director director) {
        log.info("PUT /directors: {}", director.toString());
        var result = directorService.update(director);
        log.info("completion PUT /directors: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable Long id) {
        log.info("GET /directors: {}", id);
        var director = directorService.getById(id);
        log.info("completion GET /directors: {}", director);
        return director;
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("GET /directors: all");
        var result = directorService.getAll();
        log.info("completion GET /directors: size {}", result.size());
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /directors: {}", id);
        directorService.delete(id);
        log.info("completion DELETE /directors: success");
    }
}
