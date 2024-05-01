package io.summarizeit.backend.service;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

public interface ChatService {
    public ChatResponse chat(Prompt prompt);
}
