package io.summarizeit.backend.service.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.ChatMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;

import io.summarizeit.backend.entity.Entry;
import io.summarizeit.backend.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class SummarizationTaskRunnable implements Runnable {
    private final Resource txtFile;

    private final ChatService chatService;

    private final Entry entry;

    @Override
    public void run() {
        if (entry.getTranscript() == null) {
            log.warn(String.format("Summarization for entry {} skipped because the transcript is null", entry.getId()));
        }
        String transcript = entry.getTranscript();
        List<String> stopwords = loadStopWords();

        ArrayList<String> allWords = Stream.of(transcript.toLowerCase().split(" "))
                .collect(Collectors.toCollection(ArrayList<String>::new));
        allWords.removeAll(stopwords);

        String result = allWords.stream().collect(Collectors.joining(" "));

        List<Message> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "Summarize the following transcript."));
        messages.add(new ChatMessage("user", result));
        ChatResponse response = chatService.chat(new Prompt(messages));

        entry.setBody(response.getResult().getOutput().getContent());
    }

    public List<String> loadStopWords() {
        List<String> stopWords = new ArrayList<>();

        try (InputStream inputStream = txtFile.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!StringUtils.isEmpty(line.trim())) {
                    stopWords.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stopWords;
    }

}
