package org.example.backend.exception.handler;

import org.example.backend.exception.NotFoundException;
import org.example.backend.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionUserAlreadyExistsHandler(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionNotFoundHandler(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
