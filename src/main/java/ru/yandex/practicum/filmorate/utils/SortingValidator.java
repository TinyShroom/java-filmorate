package ru.yandex.practicum.filmorate.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class SortingValidator implements ConstraintValidator<SortingConstraint, String> {

    private static final Set<String> SORTING_ID_SET = Set.of("year", "likes");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return SORTING_ID_SET.contains(s);
    }
}
