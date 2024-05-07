package io.summarizeit.backend.service.task;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.content.commons.property.PropertyPath;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.repository.EntryRepository;
import io.summarizeit.backend.repository.content.EntryContentStore;
import io.summarizeit.backend.service.TranscriptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class TranscriptionTaskRunnable implements Runnable {

    private final Entry entry;

    private final EntryContentStore entryContentStore;

    private final TranscriptionService transcriptionService;

    private final EntryRepository entryRepository;

    @Override
    public void run() {
        log.info("Starting transcript generation for entry!");
        Resource resource = entryContentStore.getResource(entry, PropertyPath.from("media"));
        Resource res;

        File tempFile;
        try {
            tempFile = File.createTempFile("summarizeit-", ".opus");
        } catch (IOException exception) {
            throw new RuntimeException("Unable to create temporary file!");
        }

        try (OutputStream outputStream = Files.newOutputStream(Path.of(tempFile.getAbsolutePath()),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            FFmpeg.atPath()
                    .addInput(UrlInput.fromUrl(resource.getFile().getAbsolutePath()))
                    .addOutput(UrlOutput.toUrl(tempFile.getAbsolutePath()))
                    .addArguments("-c:a", "libopus")
                    .addArguments("-b:a", "128k")
                    .setOverwriteOutput(true)
                    .setLogLevel(LogLevel.WARNING)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("FFmpeg error!");
        }

        try {
            res = new ByteArrayResource(Files.readAllBytes(Paths.get(tempFile.getAbsolutePath())));
        } catch (IOException exception) {
            throw new RuntimeException("Can't load opus file");
        }

        AudioTranscriptionResponse response = transcriptionService.transcribe(new AudioTranscriptionPrompt(res));
        entryContentStore.setContent(entry, PropertyPath.from("subtitle"),
                new ByteArrayInputStream(response.getResult().getOutput().getBytes()));

        List<String> result = new ArrayList<>();
        for (String line : response.getResult().getOutput().split(System.lineSeparator())) {
            if (line.isEmpty() || line.equals("WEBVTT") || line.substring(10, 13).equals("-->"))
                continue;
            result.add(line);
        }
        entry.setTranscript(String.join(" ", result));
        entryRepository.save(entry);
        log.info("Generated Transcript for entry!");
    }
}