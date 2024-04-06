package io.summarizeit.backend.service.impl;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.service.OpenAiService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {
    private final OpenAiChatClient openAiChatClient;

    private final OpenAiAudioTranscriptionClient openAiAudioTranscriptionClient;

    public AudioTranscriptionResponse transcribe(AudioTranscriptionPrompt prompt) {
        return openAiAudioTranscriptionClient.call(prompt);
    }

    public ChatResponse chat(Prompt prompt) {
        return openAiChatClient.call(prompt);
    }
}
