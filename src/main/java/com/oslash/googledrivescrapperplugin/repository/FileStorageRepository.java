package com.oslash.googledrivescrapperplugin.repository;

import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {

    Optional<FileStorage> findByUserSessionAndDriveId(UserSession userSession, String driveId);
}
