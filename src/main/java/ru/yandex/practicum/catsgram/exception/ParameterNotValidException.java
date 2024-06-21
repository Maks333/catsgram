package ru.yandex.practicum.catsgram.exception;

public class ParameterNotValidException  extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    public ParameterNotValidException(String parameter, String reason) {
        this.reason = reason;
        this.parameter = parameter;
    }
}
