package com.egodoy.alten.challenge.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

/**
 * Advisor class to manage certain Exception and transforms them into a friendly user response
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class AltenChallengeExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        AltenChallengeException error = new AltenChallengeException(AltenChallengeApiError.builder()
                .message(ex.getMessage())
                .debugMessage(ex.getLocalizedMessage())
                .status(HttpStatus.NOT_FOUND)
                .build());
        return new ResponseEntity<>(error.getApiError(), HttpStatus.NOT_FOUND);
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        AltenChallengeException error = new AltenChallengeException(AltenChallengeApiError.builder()
                .message(ex.getMessage())
                .debugMessage(ex.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build());
        return new ResponseEntity<>(error.getApiError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AltenChallengeException.class)
    public ResponseEntity<Object> handleAltenChallengeException(AltenChallengeException ex) {
        return new ResponseEntity<>(ex.getApiError(), ex.getApiError().getStatus());
    }
}
