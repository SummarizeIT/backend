package io.summarizeit.backend.service;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;

public interface OpenAiService {
    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt prompt);
    
    public ChatResponse chat(Prompt prompt);
}
