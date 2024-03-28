package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.toString());
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User changeUser(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        return userService.changeUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("GET /users: all");
        return userService.getUsers();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT /friends: {}, {}", id, friendId);
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE /friends: {}, {}", id, friendId);
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("GET /friends: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{secondId}")
    public List<User> getFriends(@PathVariable Long id, @PathVariable Long secondId) {
        log.info("GET /friends/common: {}, {}", id, secondId);
        return userService.getCommonFriends(id, secondId);
    }
}
