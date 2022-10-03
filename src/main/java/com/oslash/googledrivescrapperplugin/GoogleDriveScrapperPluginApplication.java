package com.oslash.googledrivescrapperplugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GoogleDriveScrapperPluginApplication {

    public static void main(String[] args) {

        SpringApplication.run(GoogleDriveScrapperPluginApplication.class, args);
    }

}
