package com.example.client;

import java.net.http.HttpClient;

public final class HttpClientProvider {

    private static final HttpClient INSTANCE = HttpClient.newHttpClient();

    private HttpClientProvider() {
    }

    public static HttpClient get() {
        return INSTANCE;
    }
}
