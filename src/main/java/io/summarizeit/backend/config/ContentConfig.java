package io.summarizeit.backend.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFilesystemStores
public class ContentConfig {
    @Value("${spring.content.storage-root}")
    private String storageRoot;

    @Bean
    File filesystemRoot() {
        return new File(storageRoot);
    }

    @Bean
    FileSystemResourceLoader fileSystemResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }
}
