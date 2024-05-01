package io.summarizeit.backend.service.impl;

import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.service.TranscriptionService;

@Service
@ConditionalOnProperty(name = "${app.models.transcription.mode}", havingValue = "openai")
public class OpenAITranscriptionService implements TranscriptionService {

    private OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient;

    public OpenAITranscriptionService(@Value("${app.models.transcription.api-token}") String token) {
        this.openAiAudioTranscriptionClient = new OpenAiAudioTranscriptionClient(
                new OpenAiAudioApi(token));
    }

    @Override
    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt audioTranscriptionPrompt) {
        return openAiAudioTranscriptionClient.call(audioTranscriptionPrompt);
    }

}
