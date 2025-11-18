package com.example.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NtfyEventResponse(
        String id,
        Long time,
        String event,
        String topic,
        String message,
        String title,
        List<String> tags,
        Attachment attachment
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attachment(
            String name,
            String type,
            Long size,
            Long expires,
            String url
    ) {}
}