package com.oslash.googledrivescrapperplugin.service.eventProcessingService;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;

public interface EventProcessingService {
    void lockAndProcessEvent(Credential credential, FileStorage fileStorage, int fileEventProcessingBatchSize);
}
