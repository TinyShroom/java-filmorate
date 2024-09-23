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
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FilmService filmService;
    private final FeedService feedService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User add(@Valid @RequestBody User user) {
        log.info("POST /users: {}", user.toString());
        var result = userService.addUser(user);
        log.info("completion POST /users: {}", result);
        return result;
    }

    @PutMapping
    public User change(@Valid @RequestBody User user) {
        log.info("PUT /users: {}", user.toString());
        var result = userService.changeUser(user);
        log.info("completion PUT /users: {}", result);
        return result;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET /users: all");
        var result = userService.getUsers();
        log.info("completion GET /users: size {}", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        log.info("GET /user: {}", id);
        var resultUser = userService.getById(id);
        log.info("completion GET /user: {}", resultUser);
        return resultUser;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("DELETE /users: {}", id);
        userService.delete(id);
        log.info("completion DELETE /users: success");
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT /friends: {}, {}", id, friendId);
        userService.addFriends(id, friendId);
        log.info("completion PUT /friends: success");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE /friends: {}, {}", id, friendId);
        userService.deleteFriends(id, friendId);
        log.info("completion DELETE /friends: success");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("GET /friends: {}", id);
        var result = userService.getFriends(id);
        log.info("completion GET /friends: size {}", result.size());
        return result;
    }

    @GetMapping("/{id}/friends/common/{secondId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long secondId) {
        log.info("GET /friends/common: {}, {}", id, secondId);
        var result = userService.getCommonFriends(id, secondId);
        log.info("completion GET /friends/common: size {}", result.size());
        return result;
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("GET / recommendations: {}", id);
        List<Film> resultList = filmService.getRecommendation(id);
        log.info("completion GET /recommendations: size {}", resultList.size());
        return resultList;
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getUserFeed(@PathVariable Long id) {
        log.info("GET /users/{}/feed", id);
        var result = feedService.getUserFeed(id);
        log.info("completion GET /users/{}/feed : {}", id, result);
        return result;
    }
}