package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private RatingMpa mpa;
    @Setter(AccessLevel.NONE)
    private final Set<Genre> genres;
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final Set<Long> likes;

    public Film() {
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
    }

    public boolean addGenre(Genre genre) {
        return genres.add(genre);
    }
}
