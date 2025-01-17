package ru.stepagin.backend.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EntityNotFoundException.class
    })
    public ResponseEntity<CustomProblemDetail> handleNotFound(RuntimeException ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.NOT_FOUND,
                "Not found",
                ex.getMessage()
        );
        return ResponseEntity.status(404).body(problemDetail);
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<CustomProblemDetail> handleBadRequest(RuntimeException ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for the request",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    // Обработка ошибок валидации (MethodArgumentNotValidException)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for the request",
                errorDetails
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // Обработка ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Constraint violation",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // Обработка ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomProblemDetail> handleValidationException(ValidationException ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // Обработка RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomProblemDetail> handleRuntimeException(RuntimeException ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    // Обработка общего Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomProblemDetail> handleException(Exception ex) {
        CustomProblemDetail problemDetail = new CustomProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
