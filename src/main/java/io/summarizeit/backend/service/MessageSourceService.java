package io.summarizeit.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSourceService {
    private final MessageSource messageSource;

    /**
     * Get message from message source by key.
     *
     * @param code   String
     * @param params Object[]
     * @return message String
     */
    public String get(final String code, final Object[] params) {
        try {
            return messageSource.getMessage(code, params, null);
        } catch (NoSuchMessageException e) {
            log.warn("Message not found: {}", code);
            return code;
        }
    }

    /**
     * Get message from message source by key.
     *
     * @param code String
     * @return message String
     */
    public String get(final String code) {
        return get(code, new Object[0]);
    }
}
