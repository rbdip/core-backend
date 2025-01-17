package ru.stepagin.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;

@Getter
public class CustomProblemDetail extends ProblemDetail {
    private final LocalDateTime timestamp;
    private final String message;

    public CustomProblemDetail(HttpStatus status, String detail, String customMessage) {
        this.setStatus(status.value());
        this.setTitle(status.getReasonPhrase());
        this.setDetail(detail);
        this.timestamp = LocalDateTime.now();
        this.message = customMessage;
    }
}
