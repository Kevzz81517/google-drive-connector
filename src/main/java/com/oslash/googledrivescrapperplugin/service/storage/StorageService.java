package com.oslash.googledrivescrapperplugin.service.storage;

import com.google.api.services.drive.model.File;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;

import java.util.Optional;
import java.util.UUID;

public interface StorageService {

    FileStorage store(UserSession userSession, File file, String localName, String localPath, boolean shouldLog);

    Optional<FileStorage> getByUserSessionAndDriveId(UserSession userSession, String driveId);

    FileStorage get(UUID fileStorageId);
}
