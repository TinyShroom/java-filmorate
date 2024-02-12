package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Validator;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@RestController
public class UserController {

    private final Map<Long, User> users;
    private long idCounter = 1;

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.toString());
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("POST /users: invalid birthdate " + user.getBirthday());
        }
        if (user.getId() < 1) {
            user.setId(idGenerator(users.keySet()));
        }
        users.put(user.getId(), user);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<Void> changeFilm(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        if (!Validator.isUserValid(user)) {
            throw new ValidationException("PUT /users: invalid birthdate " + user.getBirthday());
        }
        var oldUser = users.get(user.getId());
        if (oldUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BeanUtils.copyProperties(user, oldUser, "id");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(users.values()));
    }

    private long idGenerator(Set<Long> idSet) {
        while (idSet.contains(idCounter)) {
            ++idCounter;
        }
        return idCounter;
    }
}
