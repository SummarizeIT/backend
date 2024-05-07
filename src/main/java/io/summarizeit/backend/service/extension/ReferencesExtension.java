package io.summarizeit.backend.service.extension;

import java.util.Map;

import io.summarizeit.backend.dto.request.ExtensionRequest;
import io.summarizeit.backend.dto.response.ExtensionResponse;
import io.summarizeit.backend.exception.ExtensionException;
import io.summarizeit.backend.util.Command;
import io.summarizeit.backend.util.Extension;

public class ReferencesExtension implements Extension {
    private Map<String, Command> commands;

    public ReferencesExtension() {
        commands.put("generate", new GenerateReferencesCommand());
    }

    @Override
    public String getIdentifier() {
        return "references";
    }

    @Override
    public ExtensionResponse process(String command, ExtensionRequest extensionRequest) {
        Command commandHandler = commands.get(command);
        if(commandHandler == null)
            throw new ExtensionException(String.format("Command {} is not present in extension {}", command, this.getIdentifier()));
        return commandHandler.execute(extensionRequest);
    }
}

final class GenerateReferencesCommand implements Command{

    @Override
    public ExtensionResponse execute(ExtensionRequest extensionRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

}
