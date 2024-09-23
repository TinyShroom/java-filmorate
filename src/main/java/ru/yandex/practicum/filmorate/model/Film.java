package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import ru.yandex.practicum.filmorate.utils.ReleaseDateConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private Mpa mpa;
    @Setter(AccessLevel.NONE)
    private final LinkedHashSet<Genre> genres;
    @Setter(AccessLevel.NONE)
    private final LinkedHashSet<Director> directors;

    public Film() {
        this.genres = new LinkedHashSet<>();
        this.directors = new LinkedHashSet<>();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        this.directors.add(director);
    }
}
