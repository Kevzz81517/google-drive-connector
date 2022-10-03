package com.oslash.googledrivescrapperplugin.service.googleClient.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;

import java.util.List;

public interface DriveService {
    void connectRootFolder(Credential credential, UserSession userSession, String rootFolder);

    boolean resync(UserSession userSession, Credential credential, List<FileStorage> fileStorages);
}
