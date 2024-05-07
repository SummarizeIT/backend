package io.summarizeit.backend.service.extension;

import java.util.Map;

import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.exception.ExtensionException;
import io.summarizeit.backend.util.Command;
import io.summarizeit.backend.util.Extension;

public class BodyExtension implements Extension {

    private Map<String, Command> commands;

    public BodyExtension() {
        commands.put("generate", new GenerateObjectivesCommand());
    }

    @Override
    public String getIdentifier() {
        return "body";
    }

    @Override
    public ExtensionResponse process(String command, ExtensionRequest extensionRequest) {
        Command commandHandler = commands.get(command);
        if(commandHandler == null)
            throw new ExtensionException(String.format("Command {} is not present in extension {}", command, this.getIdentifier()));
        return commandHandler.execute(extensionRequest);
    }

}

final class GenerateObjectivesCommand implements Command {

    @Override
    public ExtensionResponse execute(ExtensionRequest extensionRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

}