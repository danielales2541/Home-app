package org.example.houseapp.config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.IOException;
import java.util.List;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.service-account-file}")
    private String serviceAccountFile;

    private final ResourceLoader resourceLoader;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Verifica si ya existe una app inicializada
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        if (!firebaseApps.isEmpty()) {
            return firebaseApps.get(0);
        }

        Resource resource = resourceLoader.getResource("classpath:" + serviceAccountFile);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();
        return FirebaseApp.initializeApp(options);
    }
}
