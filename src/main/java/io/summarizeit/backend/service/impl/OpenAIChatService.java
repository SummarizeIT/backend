package io.summarizeit.backend.service.impl;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import io.summarizeit.backend.service.ChatService;

@Service
@ConditionalOnProperty(name = "app.models.chat.mode", havingValue = "openai")
public class OpenAIChatService implements ChatService {
    private OpenAiChatClient openAiChatClient;

    public OpenAIChatService(@Value("${app.models.chat.api-token}") String token) {
        this.openAiChatClient = new OpenAiChatClient(new OpenAiApi(token));
    }

    @Override
    public ChatResponse chat(Prompt prompt) {
        return openAiChatClient.call(prompt);
    }

}
