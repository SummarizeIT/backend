package io.summarizeit.backend.exception;

import java.io.Serial;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExtensionException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public ExtensionException() {
        super("Extension error!!");
    }

    public ExtensionException(final String message) {
        super(message);
    }
}
