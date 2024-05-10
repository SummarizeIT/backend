package io.summarizeit.backend.service.impl;

import java.util.List;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import io.summarizeit.backend.service.ChatService;

@Service
@ConditionalOnProperty(name = "app.models.chat.mode", havingValue = "ollama")
public class LlamaService implements ChatService {
    @Value("${app.models.chat.model-name}")
    private String modelName;

    private OpenAiChatClient openAiChatClient;

    public LlamaService(@Value("${app.models.chat.api-base-url}") String baseurl,
            @Value("${app.models.chat.api-token}") String token) {
        this.openAiChatClient = new OpenAiChatClient(
                new OpenAiApi(baseurl, token, RestClient.builder(), RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER));
    }

    @Override
    public ChatResponse chat(List<Message> messages) {
        return openAiChatClient.call(new Prompt(messages, OpenAiChatOptions.builder().withModel(modelName).build()));
    }

}
