package io.summarizeit.backend.util;

import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;

public interface Extension {
    public String getIdentifier();

    public ExtensionResponse process(String command, ExtensionRequest extensionRequest);  
}
