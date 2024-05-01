package io.summarizeit.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileSystemException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileSystemException() {
        super("File-system exception!");
    }

    public FileSystemException(final String message) {
        super(message);
    }
}
