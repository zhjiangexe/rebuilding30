package com.rebuilding.day.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.rebuilding.day.config.TestContextConfig.objectMapper;

public final class WebTestUtil {

    private WebTestUtil() {
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = objectMapper();
        return mapper.writeValueAsBytes(object);
    }

    public static String createStringWithLength(int length) {
        StringBuilder string = new StringBuilder();
        for (int index = 0; index < length; index++) {
            string.append("a");
        }
        return string.toString();
    }
}
