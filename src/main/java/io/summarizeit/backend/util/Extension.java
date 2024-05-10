package io.summarizeit.backend.util;

import io.summarizeit.backend.dto.request.ExtensionPayload;
import io.summarizeit.backend.dto.response.ExtensionResponse;


public interface Extension {
    public abstract String getIdentifier();

    public abstract ExtensionResponse process(String command, ExtensionPayload ExtensionPayload, ExtensionContext extensionContext);
}
