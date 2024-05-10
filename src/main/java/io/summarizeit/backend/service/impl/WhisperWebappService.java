package io.summarizeit.backend.service.impl;

import java.io.IOException;
import java.net.URI;

import org.springframework.ai.openai.audio.transcription.AudioTranscription;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.summarizeit.backend.service.TranscriptionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.models.transcription.mode", havingValue = "whisper-webapp")
public class WhisperWebappService implements TranscriptionService {
    private final RestTemplate restTemplate;

    @Value("${app.models.transcription.api-base-url}")
    private String baseurl;

    @Override
    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt audioTranscriptionPrompt)
            throws RuntimeException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource res;
        try {
            res = new ByteArrayResource(audioTranscriptionPrompt.getInstructions().getInputStream().readAllBytes()) {
                @Override
                public String getFilename() {
                    return "media";
                }
            };
        } catch (IOException exception) {
            throw new RuntimeException("Couldn't obtain file to send to transcription service!");
        }
        body.add("audio_file", res);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseurl + "/asr");
        builder.queryParam("output", "vtt");
        builder.queryParam("task", "transcribe");
        builder.queryParam("language", "ar");
        builder.queryParam("vad_filter", "true");
        builder.queryParam("encode", "true");
        URI uri = builder.build().encode().toUri();

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri, HttpMethod.POST, requestEntity,
                String.class);

        // Process the response
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return new AudioTranscriptionResponse(new AudioTranscription(responseEntity.getBody()));
        } else {
            throw new RuntimeException("Transcript backend failed!");
        }
    }

}
