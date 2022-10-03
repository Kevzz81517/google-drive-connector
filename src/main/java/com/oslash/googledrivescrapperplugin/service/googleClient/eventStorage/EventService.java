package com.oslash.googledrivescrapperplugin.service.googleClient.eventStorage;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;

public interface EventService {
    void storeEventAndProcess(String eventId, FileStorage fileStorage, Credential credential);
}
