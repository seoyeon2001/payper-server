package com.payper.global.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FcmConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            String envPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
            if (envPath != null && !envPath.isEmpty()) {
                // 운영 환경: 환경 변수 경로 사용
                serviceAccount = new FileInputStream(envPath);
            } else {
                // 로컬 환경: resources/firebase/ 경로 사용
                serviceAccount = new ClassPathResource("firebase/firebase-service-account.json")
                        .getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
        return FirebaseMessaging.getInstance();
    }
}
