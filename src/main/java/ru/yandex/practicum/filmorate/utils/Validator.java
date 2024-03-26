package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class Validator {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public static boolean isFilmValid(Film film) {
        if (film.getReleaseDate() == null) return true;
        return !CINEMA_BIRTHDAY.isAfter(film.getReleaseDate());
    }

    public static boolean isUserValid(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthdate() == null) return true;
        return !LocalDate.now().isBefore(user.getBirthdate());
    }
}
