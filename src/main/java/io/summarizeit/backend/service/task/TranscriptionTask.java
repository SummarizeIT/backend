package io.summarizeit.backend.service.task;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.property.PropertyPath;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.PipeInput;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.repository.content.EntryContentStore;
import io.summarizeit.backend.service.TranscriptionService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TranscriptionTask implements Runnable {

    private final Entry entry;

    @Autowired
    private EntryContentStore entryContentStore;

    @Autowired
    private TranscriptionService transcriptionService;

    @Override
    public void run() {
        InputStream inputStream = entryContentStore.getContent(entry, PropertyPath.from("media"));
        Resource res = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FFmpeg.atPath()
                    .addInput(PipeInput.pumpFrom(inputStream))
                    .addOutput(PipeOutput.pumpTo(outputStream))
                    .addArguments("-c:a", "libopus")
                    .addArguments("-b:a", "128k")
                    .execute();

            byte[] outputBytes = outputStream.toByteArray();
            res = new ByteArrayResource(outputBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioTranscriptionResponse response = transcriptionService.transcribe(new AudioTranscriptionPrompt(res));
        entry.setTranscript(response.getResult().getOutput());
    }

}
