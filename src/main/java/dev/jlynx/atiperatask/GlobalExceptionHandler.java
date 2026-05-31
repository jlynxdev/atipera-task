package dev.jlynx.atiperatask;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AtiperaTaskException.class)
    ResponseEntity<ErrorResponse> handleAtiperaTaskException(AtiperaTaskException e) {
        ErrorResponse responseBody = new ErrorResponse(e.getStatus().value(), e.getMessage());
        return new ResponseEntity<>(responseBody, e.getStatus());
    }
}
