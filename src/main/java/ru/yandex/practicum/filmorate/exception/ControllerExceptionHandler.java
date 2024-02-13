package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage exceptionHandler(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Validation exception");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorMessage exceptionHandler(ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Validation exception");
    }
}