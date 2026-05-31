package dev.jlynx.atiperatask;

import org.springframework.http.HttpStatus;

class UsernameNotFoundException extends AtiperaTaskException {
    UsernameNotFoundException(String username) {
        String reason = "GitHub username '" + username + "' not found.";
        super(reason, HttpStatus.NOT_FOUND);
    }
}
