package com.example.client;

import com.example.domain.ChatModel;
import com.example.domain.NtfyEventResponse;
import com.example.domain.NtfyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public record NtfyHttpClient(ChatModel model) implements ChatNetworkClient {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger("NtfyClient");

    @Override
    public Subscription subscribe(String baseUrl, String topic) {

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl).resolve(topic + "/json"))
                .header("accept", "application/json")
                .GET()
                .build();

        CompletableFuture<HttpResponse<Stream<String>>> future =
                HttpClientProvider.get().sendAsync(req, HttpResponse.BodyHandlers.ofLines());

        AtomicBoolean open = new AtomicBoolean(true);

        future.thenAccept(response -> {
            response.body().forEach(line -> {

                log.debug("Raw event received: {}", line);

                if (!open.get()) return;

                try {
                    NtfyEventResponse msg = mapper.readValue(line, NtfyEventResponse.class);
                    if (msg.event().equals("message")) {
                        model.addMessage(msg);
                        log.info("Message added: {}", msg);
                    }
                } catch (JsonProcessingException e) {
                    log.error("Error parsing event: {}", line, e);
                }
            });
        }).exceptionally(ex -> {
            log.error("Error while subscribing to topic {}", topic, ex);
            open.set(false);
            return null;
        });
        log.info("Subscribing to topic: {}", topic);

        return new Subscription() {
            @Override
            public void close() {
                open.set(false);
                future.cancel(true);
            }

            @Override
            public boolean isOpen() {
                return open.get();
            }
        };
    }

    @Override
    public void send(String baseUrl, NtfyMessage msg, File attachment) throws IOException, InterruptedException {

        if (attachment != null) {
            sendWithAttachment(baseUrl, msg, attachment);
            return;
        }

        sendJsonOnly(baseUrl, msg);
    }

    private void sendJsonOnly(String baseUrl, NtfyMessage msg) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(msg);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        var response = HttpClientProvider.get().send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            log.debug("Sent message payload: {}", json);
            log.info("Message sent");
        }
        log.error("Failed to send message payload: {}", json);
        throw new IOException("Failed to send message payload: " + statusCode);
    }

    private void sendWithAttachment(String baseUrl, NtfyMessage msg, File file)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl).resolve((msg.topic())))
                .header("Filename", file.getName())
                .PUT(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                .build();


        var response = HttpClientProvider.get().send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        if (200 <= statusCode && statusCode < 300) {
            log.debug("Attachment sent: {}", statusCode);
            log.info("status: {}", statusCode);
        }
        log.error("Failed to send attachment: {}", statusCode);
        throw new IOException("Failed to send attachment: " + statusCode);

    }


}
