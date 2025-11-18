package com.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NtfyMessage(
        String id,
        Long time,
        String event,
        String topic,
        String message,
        String title,
        List<String> tags,
        String attach,
        String filename
) {
    public static class Builder {
        private String id;
        private Long time;
        private String event;
        private String topic;
        private String message;
        private String title;
        private List<String> tags;
        private String attach;
        private String filename;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder attach(String attach) { this.attach = attach; return this; }
        public Builder filename(String filename) { this.filename = filename; return this; }

        public NtfyMessage build() {
            return new NtfyMessage(id, time, event, topic, message, title, tags, attach, filename);
        }
    }
}
