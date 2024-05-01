package io.summarizeit.backend.service.impl;

import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.service.TranscriptionService;

@Service
@ConditionalOnProperty(name = "${app.models.transcription.mode}", havingValue = "whisper-webapp")
public class WhisperWebappService implements TranscriptionService {

    @Override
    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt audioTranscriptionPrompt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'transcribe'");
    }
    
}
