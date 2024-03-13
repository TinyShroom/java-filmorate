package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");
    private final String description;

}
