package io.summarizeit.backend.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SynchronousTaskService {
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private final ThreadPoolTaskExecutor taskExecutor;

    @PostConstruct
    public void init(){
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
    }
    
    public void addTask(Runnable task) {
        taskQueue.offer(task);
        if (taskQueue.size() == 1) {
            executeNextTask();
        }
    }

    private void executeNextTask() {
        Runnable nextTask = taskQueue.peek();
        if (nextTask != null) {
            taskExecutor.execute(() -> {
                nextTask.run();
                taskQueue.poll(); // Remove the task after execution
                executeNextTask(); // Execute the next task recursively
            });
        }
    }
}