package ru.yandex.practicum.filmorate.exception;

public class ErrorMessage {

    private final int code;
    private final String message;

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}