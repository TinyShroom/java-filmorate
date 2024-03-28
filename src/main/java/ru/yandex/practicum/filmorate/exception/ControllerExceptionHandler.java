package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(ValidationException e) {
        log.warn("ValidationException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: " + e.getMessage());
        return Map.of("message", "Validation exception");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(NotFoundException e) {
        log.warn("NotFoundException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(DatabaseConstraintException e) {
        log.warn("DatabaseConstraintException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(EmptyResultDataAccessException e) {
        log.warn("EmptyResultDataAccessException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(DataIntegrityViolationException e) {
        log.warn("DataIntegrityViolationException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> exceptionHandler(RuntimeException e) {
        log.warn("RuntimeException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }
}