package com.example;

import com.example.domain.ChatModel;
import com.example.domain.NtfyEventResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatModelTest {

    @Test
    void addMessageShouldAppend() {
        ChatModel model = new ChatModel();

        NtfyEventResponse msg = new NtfyEventResponse(
                "1", 100L, "message", "topic", "Hello", null, null, null
        );

        model.addMessage(msg);

        assertEquals(1, model.getMessages().size());
        assertEquals(msg, model.getMessages().getFirst());
    }

    @Test
    void addMessageFromBackgroundThreadShouldWork() throws InterruptedException {
        ChatModel model = new ChatModel();

        NtfyEventResponse msg = new NtfyEventResponse(
                "2", 200L, "message", "topic", "Background", null, null, null
        );

        Thread t = new Thread(() -> model.addMessage(msg));
        t.start();
        t.join();

        // Allow queue to be processed before proceeding to assert
        Thread.sleep(1000);

        assertEquals(1, model.getMessages().size());
    }
}
