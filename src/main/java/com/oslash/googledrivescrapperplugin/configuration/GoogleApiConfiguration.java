package com.oslash.googledrivescrapperplugin.configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/***
 * Configurations for using the Google APIs
 */
@Slf4j
@Configuration
public class GoogleApiConfiguration {

    /**
     * Location to the Google Credentials File required for Google API authentication
     */
    @Value("${app.google-services.client.credentials.path}")
    private Path credentialsPath;

    /**
     * Authorization Scopes Required by the Application.
     * By default https://www.googleapis.com/auth/drive is configured
     */
    @Getter
    @Value("${app.google-services.client.auth.scope:https://www.googleapis.com/auth/drive}")
    private List<String> scopes;

    @Bean
    public GoogleClientSecrets clientSecrets() {

        try (InputStream in = new FileInputStream(credentialsPath.toFile())) {

            return GoogleClientSecrets.load(jsonFactory(), new InputStreamReader(in));
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() {
        try {
            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport(), jsonFactory(),
                    clientSecrets(),
                    scopes
            )
                    .setAccessType("offline")
                    .build();
        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }

    @Bean
    public JsonFactory jsonFactory() {

        return new GsonFactory();
    }

    @Bean
    public HttpTransport httpTransport() {

        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }
}
