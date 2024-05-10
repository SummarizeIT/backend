package io.summarizeit.backend.service.extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.context.ApplicationContext;

import io.summarizeit.backend.dto.request.ExtensionPayload;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.exception.ExtensionException;
import io.summarizeit.backend.service.ChatService;
import io.summarizeit.backend.util.Command;
import io.summarizeit.backend.util.Extension;
import io.summarizeit.backend.util.ExtensionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ObjectivesExtension implements Extension {
    private Map<String, Command> commands = new HashMap<>();

    public ObjectivesExtension(ApplicationContext applicationContext) {
        ChatService chatService = applicationContext.getBean(ChatService.class);
        commands.put("generate", new GenerateObjectivesCommand(chatService));
    }

    @Override
    public String getIdentifier() {
        return "objectives";
    }

    @Override
    public ExtensionResponse process(String command, ExtensionPayload extensionRequest,
            ExtensionContext extensionContext) {
        Command commandHandler = commands.get(command);
        if (commandHandler == null)
            throw new ExtensionException(
                    String.format("Command {} is not present in extension {}", command, this.getIdentifier()));
        return commandHandler.execute(extensionRequest, extensionContext);
    }
}

@AllArgsConstructor
final class GenerateObjectivesCommand implements Command {
    private final ChatService chatService;

    @Override
    public ExtensionResponse execute(ExtensionPayload extensionRequest, ExtensionContext extensionContext) {
        Message systemMessage = new SystemMessage(
                "Generate an a list of objectives from the transcript of the following transcription for a video. Make sure it's in the form: {This is the first objective},{This is the second objective}");
        Message userMessage = new UserMessage(extensionContext.getEntry().getTranscript());

        ChatResponse response = chatService.chat(List.of(systemMessage, userMessage));

        return new GenerateObjectivesResponse(response.getResult().getOutput().getContent());
    }

}

@Getter
@Setter
@AllArgsConstructor
final class GenerateObjectivesResponse implements ExtensionResponse {
    private String text;
}