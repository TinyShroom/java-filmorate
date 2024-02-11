package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final Map<Long, User> users;

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(@Valid @RequestBody User user) {
        users.put(user.getId(), user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/user")
    public ResponseEntity<Void> changeFilm(@Valid @RequestBody User user) {
        var oldUser = users.get(user.getId());
        if (oldUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BeanUtils.copyProperties(user, oldUser, "id");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<>(users.values()));
    }
}
