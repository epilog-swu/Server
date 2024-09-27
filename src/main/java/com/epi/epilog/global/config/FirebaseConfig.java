package com.epi.epilog.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FirebaseConfig {
    @Value("${firebase.json}")
    private String firebaseJson;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(firebaseJson);
            String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);

            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decodedBytes);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
