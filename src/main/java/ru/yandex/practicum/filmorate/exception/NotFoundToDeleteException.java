package ru.yandex.practicum.filmorate.exception;

public class NotFoundToDeleteException extends RuntimeException {
    public NotFoundToDeleteException(String message) {
        super(message);
    }
}
