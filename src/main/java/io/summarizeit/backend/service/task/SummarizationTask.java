package io.summarizeit.backend.service.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.service.ChatService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SummarizationTask {
    @Value("classpath:words.txt")
    private Resource txtFile;

    private final ChatService chatService;

    public Runnable getRunnable(Entry entry) {
        return new SummarizationTaskRunnable(txtFile, chatService, entry);
    }
}
