package io.summarizeit.backend.util;

import io.summarizeit.backend.dto.request.ExtensionPayload;
import io.summarizeit.backend.dto.response.ExtensionResponse;

public interface Command {
    ExtensionResponse execute(ExtensionPayload ExtensionPayload, ExtensionContext extensionContext);
}