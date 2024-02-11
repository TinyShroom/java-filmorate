package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class Validator {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static boolean filmValidator(Film film) {
        return CINEMA_BIRTHDAY.isAfter(film.getReleaseDate());
    }

    public static boolean userValidator(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return LocalDate.now().isBefore(user.getBirthday());
    }
}
