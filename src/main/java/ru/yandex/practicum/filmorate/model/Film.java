package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
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
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private RatingMpa ratingMpa;
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final Set<Long> likes;
    @Setter(AccessLevel.NONE)
    private final Set<Genre> genre;

    public Film() {
        this.likes = new HashSet<>();
        this.genre = new HashSet<>();
    }

}
