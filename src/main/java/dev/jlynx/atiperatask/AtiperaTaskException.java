package dev.jlynx.atiperatask;

import org.springframework.http.HttpStatus;

abstract class AtiperaTaskException extends RuntimeException {

    private final HttpStatus status;

    AtiperaTaskException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
