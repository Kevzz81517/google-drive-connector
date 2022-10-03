package com.oslash.googledrivescrapperplugin.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.service.googleClient.credential.CredentialService;
import com.oslash.googledrivescrapperplugin.service.googleClient.eventStorage.EventService;
import com.oslash.googledrivescrapperplugin.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
public class EventListenerController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CredentialService credentialService;


    /**
     * Google API Resource Watch Listener
     * The URL needs to be configured in the app.google-services.client.file-event.webhook-uri
     *
     * @param resourceState
     * @param eventId
     * @param channelId
     * @param changedComponent
     */
    @PostMapping("${app.google-services.client.file-event.webhook-uri}")
    public void receiveChangeEvent(@RequestHeader(value = "x-goog-resource-state") String resourceState,
                                   @RequestHeader(value = "x-goog-message-number") String eventId,
                                   @RequestHeader(value = "x-goog-channel-id") String channelId,
                                   @RequestHeader(value = "x-goog-changed", required = false) String changedComponent) {

        /** NOTE: Google 'add' resource state was not coming, so used the update & children combination to
         identify the new file addition
         */
        if (resourceState.equals("update") && changedComponent.equals("children")) {
            String[] channelMeta = channelId.split("\\+");
            UUID fileStorageId = UUID.fromString(channelMeta[0]);
            FileStorage fileStorage = this.storageService.get(fileStorageId);
            Credential credential = this.credentialService.getCredential(fileStorage.getUserSession().getAccessToken(), fileStorage.getUserSession().getRefreshToken());
            log.info("Received Event For USER ID {} File Storage ID {}", fileStorage.getUserSession().getUserId(), fileStorage.getId());
            this.eventService.storeEventAndProcess(eventId, fileStorage, credential);
        }
    }
}
