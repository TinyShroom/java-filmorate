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
        return ResponseEntity.status(HttpStatus.OK).body(userService.addUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity<User> changeUser(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        return new ResponseEntity<>(userService.changeUser(user), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT /friends: {}, {}", id, friendId);
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE /friends: {}, {}", id, friendId);
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable Long id) {
        log.info("GET /friends: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{secondId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable Long id, @PathVariable Long secondId) {
        log.info("GET /friends/common: {}, {}", id, secondId);
        return userService.getCommonFriends(id, secondId);
    }
}
