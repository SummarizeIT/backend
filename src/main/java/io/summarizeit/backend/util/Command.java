package io.summarizeit.backend.util;

import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;

public interface Command {
    ExtensionResponse execute(ExtensionRequest extensionRequest);
}