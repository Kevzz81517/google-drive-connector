package com.oslash.googledrivescrapperplugin.service.googleClient.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;

public interface DriveService {
    void connectRootFolder(Credential credential, UserSession userSession, String rootFolder);
}
