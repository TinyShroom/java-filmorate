package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ErrorDto;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorDto exceptionHandler(ValidationException e) {
        log.info("ValidationException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorDto exceptionHandler(MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException: " + e.getMessage());
        return new ErrorDto("Validation exception");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorDto exceptionHandler(NotFoundException e) {
        log.info("NotFoundException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorDto exceptionHandler(ConstraintException e) {
        log.info("DatabaseConstraintException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorDto exceptionHandler(EmptyResultDataAccessException e) {
        log.info("EmptyResultDataAccessException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorDto exceptionHandler(DataIntegrityViolationException e) {
        log.info("DataIntegrityViolationException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorDto exceptionHandler(DuplicateKeyException e) {
        log.info("DuplicateKeyException: " + e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorDto exceptionHandler(Exception e) {
        log.error("Exception: " + e.getMessage(), e);
        return new ErrorDto(e.getMessage());
    }
}