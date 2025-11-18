package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static final Logger log = LoggerFactory.getLogger(EnvLoader.class);

    public static Properties loadEnv() {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
        } catch (FileNotFoundException e) {
            log.error("Could not load .env file", e);
        } catch (IOException e) {
            log.error("Failed to load env file: ", e);
        }

        return props;
    }
}