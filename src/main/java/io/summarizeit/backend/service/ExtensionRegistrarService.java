package io.summarizeit.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.dto.request.ExtensionPayload;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.exception.ExtensionException;
import io.summarizeit.backend.util.Extension;
import io.summarizeit.backend.util.ExtensionContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtensionRegistrarService {

    private Map<String, Extension> extensions = new HashMap<>();

    private final MessageSourceService messageSourceService;

    private final ApplicationContext applicationContext;

    private List<Extension> findAndInstanciateExtensions() {
        List<Extension> instances = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Extension.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("")) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                Object instance = clazz.getDeclaredConstructor(ApplicationContext.class)
                        .newInstance(applicationContext);
                instances.add((Extension) instance);
            } catch (Exception e) {
                throw new RuntimeException("Error scanning for extensions!", e.getCause());
            }
        }
        return instances;
    }

    @PostConstruct
    public void init() {
        List<Extension> instances = findAndInstanciateExtensions();
        instances.forEach(extensionService -> extensions.put(extensionService.getIdentifier(), extensionService));
        log.info(String.format("Loaded %d Extension(s)!", extensions.size()));
        for (String identifier : extensions.keySet()) {
            log.info("Extension {} loaded", identifier);
        }
    }

    public ExtensionResponse call(String identifier, String command, ExtensionPayload ExtensionPayload,
            ExtensionContext extensionContext) {
        Extension extension = extensions.get(identifier);
        if (extension == null)
            throw new ExtensionException(messageSourceService.get("extension-invalid"));

        return extension.process(command, ExtensionPayload, extensionContext);
    }

    public List<String> getExtensionIdentifiers() {
        return extensions.keySet().stream().toList();
    }
}