package io.summarizeit.backend.service;

import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;

public interface TranscriptionService {
    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt audioTranscriptionPrompt);
}
