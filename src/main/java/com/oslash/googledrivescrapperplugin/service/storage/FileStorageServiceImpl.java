package com.oslash.googledrivescrapperplugin.service.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.File;
import com.oslash.googledrivescrapperplugin.exception.ElementNotFoundException;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import com.oslash.googledrivescrapperplugin.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements StorageService {

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.data.storage.root-path:data}")
    private Path dataStorageRootPath;

    @Override
    public FileStorage store(UserSession userSession, File file, String localName, String localPath, boolean shouldLog) {

        Calendar createdOn = Calendar.getInstance();
        createdOn.setTimeInMillis(file.getCreatedTime().getValue());

        try {
            return this.fileStorageRepository.save(
                    FileStorage.builder()
                            .name(file.getName())
                            .userSession(userSession)
                            .createdTime(createdOn)
                            .driveId(file.getId())
                            .extension(file.getFileExtension())
                            .isTrashed(file.getTrashed())
                            .mimeType(file.getMimeType())
                            .owners(this.objectMapper.writeValueAsString(file.getOwners()))
                            .localPath(localPath)
                            .size(file.getSize())
                            .build()
            );
        } catch (JsonProcessingException ex) {

            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<FileStorage> getByUserSessionAndDriveId(UserSession userSession, String driveId) {

        return this.fileStorageRepository.findByUserSessionAndDriveId(userSession, driveId);
    }

    @Override
    public FileStorage get(UUID fileStorageId) {

        Optional<FileStorage> fileStorage = this.fileStorageRepository.findById(fileStorageId);
        if (fileStorage.isPresent()) {

            return fileStorage.get();
        } else {

            throw new ElementNotFoundException("File Storage Not Found with ID " + fileStorageId);
        }

    }

}
