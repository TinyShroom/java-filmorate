package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.toString());
        var addedUser = userService.addUser(user);
        System.out.println(addedUser);
        return ResponseEntity.status(HttpStatus.OK).body(addedUser);
    }

    @PutMapping("/users")
    public ResponseEntity<User> changeFilm(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        return new ResponseEntity<>(userService.changeUser(user), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }
}
