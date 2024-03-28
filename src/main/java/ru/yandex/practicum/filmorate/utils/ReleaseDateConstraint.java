package ru.yandex.practicum.filmorate.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

// https://www.baeldung.com/spring-mvc-custom-validator
@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateConstraint {

    String message() default "Release date must be after 1895-12-27";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
