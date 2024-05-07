package io.summarizeit.backend.service;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.exception.ExtensionException;
import io.summarizeit.backend.util.Extension;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExtensionRegistrarService {

    private Map<String, Extension> extensions;

    private final ApplicationContext applicationContext;

    private final MessageSourceService messageSourceService;

    @PostConstruct
    public void init() {
        Map<String, Extension> beans = applicationContext.getBeansOfType(Extension.class);
        beans.values().forEach(extensionService -> extensions.put(extensionService.getIdentifier(), extensionService));
    }

    public ExtensionResponse call(String identifier, String command, ExtensionRequest extensionRequest){
        Extension extension = extensions.get(identifier);
        if(extension == null)
            throw new ExtensionException(messageSourceService.get("extension-invalid"));
        
        return extension.process(command, extensionRequest);
    }

}
