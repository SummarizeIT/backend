package io.summarizeit.backend.service;

import java.util.List;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;

public interface ChatService {
    public ChatResponse chat(List<Message> messages);
}
