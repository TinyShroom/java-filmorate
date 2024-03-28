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
    public User add(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.toString());
        var result = userService.addUser(user);
        log.info("completion POST /users: {}", result);
        return result;
    }

    @PutMapping("/users")
    public User change(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        var result = userService.changeUser(user);
        log.info("completion PUT /users: {}", result);
        return result;
    }

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("GET /users: all");
        var result = userService.getUsers();
        log.info("completion GET /users: size {}", result.size());
        return result;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT /friends: {}, {}", id, friendId);
        userService.addFriends(id, friendId);
        log.info("completion PUT /friends: success");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE /friends: {}, {}", id, friendId);
        userService.deleteFriends(id, friendId);
        log.info("completion DELETE /friends: success");
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("GET /friends: {}", id);
        var result = userService.getFriends(id);
        log.info("completion GET /friends: size {}", result.size());
        return result;
    }

    @GetMapping("/users/{id}/friends/common/{secondId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long secondId) {
        log.info("GET /friends/common: {}, {}", id, secondId);
        var result = userService.getCommonFriends(id, secondId);
        log.info("GET /friends/common: size {}", result.size());
        return result;
    }
}
