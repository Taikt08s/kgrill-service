package com.swd392.group2.kgrill_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
public class FirebaseConfiguration {
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        Map<String, String> env = System.getenv();

        // Ensure private key formatting
        String privateKey = env.get("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");

        // Prepare JSON credentials string
        String jsonCredentials = String.format(
                "{ \"type\": \"%s\", \"project_id\": \"%s\", \"private_key_id\": \"%s\", \"private_key\": \"%s\", \"client_email\": \"%s\", \"client_id\": \"%s\", \"auth_uri\": \"%s\", \"token_uri\": \"%s\", \"auth_provider_x509_cert_url\": \"%s\", \"client_x509_cert_url\": \"%s\" }",
                env.get("FIREBASE_TYPE"),
                env.get("FIREBASE_PROJECT_ID"),
                env.get("FIREBASE_PRIVATE_KEY_ID"),
                privateKey,
                env.get("FIREBASE_CLIENT_EMAIL"),
                env.get("FIREBASE_CLIENT_ID"),
                env.get("FIREBASE_AUTH_URI"),
                env.get("FIREBASE_TOKEN_URI"),
                env.get("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"),
                env.get("FIREBASE_CLIENT_X509_CERT_URL")
        );

        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8));

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentialsStream);

        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "k-grill");

        return FirebaseMessaging.getInstance(app);
    }
}
