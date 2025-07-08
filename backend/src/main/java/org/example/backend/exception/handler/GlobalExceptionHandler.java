package org.example.backend.exception.handler;

import org.example.backend.exception.NotFoundException;
import org.example.backend.exception.AlreadyExistsException;
import org.example.backend.exception.UserAlreadyLoggedInException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionAlreadyExistsHandler(AlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionNotFoundHandler(NotFoundException ex) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyLoggedInException.class)
    public ResponseEntity<ErrorDetails> exceptionUserAlreadyLoggedInHandler(UserAlreadyLoggedInException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
