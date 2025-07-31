package com.payper.external.openai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GptConfig {
    public static String getApiKey() {
        try (InputStream input = GptConfig.class.getClassLoader().getResourceAsStream("application-db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("openai.api.key");
        } catch (IOException e) {
            throw new RuntimeException("API key 불러오기 실패", e);
        }
    }

    public static String getApiUrl() {
        try (InputStream input = GptConfig.class.getClassLoader().getResourceAsStream("application-db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("openai.api.url");
        } catch (IOException e) {
            throw new RuntimeException("API url 불러오기 실패", e);
        }
    }
}


