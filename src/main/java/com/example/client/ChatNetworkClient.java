package com.example.client;


import com.example.domain.NtfyMessage;

import java.io.File;
import java.io.IOException;

public interface ChatNetworkClient {
    Subscription subscribe(String baseUrl, String topic);
    void send(String baseUrl, NtfyMessage message, File file) throws IOException, InterruptedException;
    interface Subscription extends AutoCloseable {
        @Override
        void close();
        boolean isOpen();
    }
}
