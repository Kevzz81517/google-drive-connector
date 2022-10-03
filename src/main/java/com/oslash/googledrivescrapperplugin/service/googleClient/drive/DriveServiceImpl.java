package com.oslash.googledrivescrapperplugin.service.googleClient.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.oslash.googledrivescrapperplugin.exception.ElementNotFoundException;
import com.oslash.googledrivescrapperplugin.exception.MultipleElementFoundException;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import com.oslash.googledrivescrapperplugin.repository.UserSessionRepository;
import com.oslash.googledrivescrapperplugin.service.storage.StorageService;
import com.oslash.googledrivescrapperplugin.util.UUIDUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DriveServiceImpl implements DriveService {

    @Autowired
    private HttpTransport httpTransport;

    @Autowired
    private JsonFactory jsonFactory;

    @Autowired
    private GoogleClientSecrets clientSecrets;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Value("${app.data.storage.root-path:data1}")
    private Path dataStorageRootPath;

    @Value("${app.data.storage.batch-size:100}")
    private int dataStorageBatchSize;

    @Value("#{'${app.google-services.client.file-event.webhook-host}' + '${app.google-services.client.file-event.webhook-uri}'}")
    private String eventChangeWebhookUri;

    @Autowired
    private StorageService storageService;

    /**
     * Query template to find the folders with given name
     */
    private static String SEARCH_FOLDER_QUERY = "mimeType='application/vnd.google-apps.folder' and trashed=false and name = '%s'";

    /**
     * Query template to find the folders from given name list
     */
    private static String SEARCH_FOLDERS_BY_NAMES_QUERY = "mimeType='application/vnd.google-apps.folder' and trashed=false and name in %s";

    /**
     * Query template to find the files by Parent folder id
     */
    private static String SEARCH_FILES_BY_PARENT_FOLDER_QUERY = "'%s' in parents and trashed=false";

    /**
     * Google API List Files Query
     *
     * @param service  Drive Service
     * @param query    Filtering Criteria for the files
     * @param pageSize Optional Page size parameter
     * @return FileList
     * @throws IOException
     */
    private FileList queryFiles(Drive service, String query, Integer pageSize) throws IOException {

        Drive.Files.List listQuery = service.files().list()
                .setQ(query)
                .setFields("nextPageToken, files(*)");

        if (pageSize != null) {

            listQuery.setPageSize(pageSize);
        }

        return listQuery.execute();
    }

    /**
     * Get the Folders with provided FileStorages
     *
     * @param service
     * @param fileStorages
     * @return
     * @throws IOException
     */
    private List<File> findFolders(Drive service, List<FileStorage> fileStorages) throws IOException {

        String nameParameter = "'" + String.join("','",
                fileStorages.stream().map(fileStorage -> fileStorage.getName()).collect(Collectors.toSet())) + "'";

        String pageToken = null;

        List<File> actualRootFolder = new ArrayList<>();

        do {
            FileList result = this.queryFiles(service, String.format(SEARCH_FOLDERS_BY_NAMES_QUERY, nameParameter), null);
            actualRootFolder.addAll(
                    result.getFiles().stream()
                            .filter(fileItem -> fileStorages.stream()
                                    .anyMatch(fileStorage -> fileStorage.getDriveId().equals(fileItem.getId())))
                            .collect(Collectors.toSet())
            );
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return actualRootFolder;
    }

    private List<File> findFolders(Drive service, String rootFolder, int pageSize) throws IOException {
        String pageToken = null;

        List<File> actualRootFolder = new ArrayList<>();

        do {
            FileList result = this.queryFiles(
                    service,
                    String.format(SEARCH_FOLDER_QUERY, rootFolder),
                    pageSize
            );
            actualRootFolder.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return actualRootFolder;
    }

    public void recursiveBackup(UserSession userSession, Credential credential, Drive service, String rootId, Path root, int pageSize, boolean shouldLog) {

        String pageToken = null;
        try {
            do {
                FileList childrenFiles = this.queryFiles(service, String.format(SEARCH_FILES_BY_PARENT_FOLDER_QUERY, rootId), pageSize);
                childrenFiles.getFiles().stream().parallel().forEach(fileItem -> {
                    try {

                        String actualName = null;
                        Path localPath = null;
                        boolean isFolder = fileItem.getMimeType().equals("application/vnd.google-apps.folder");

                        if (storageService.getByUserSessionAndDriveId(userSession, fileItem.getId()).isEmpty()) {
                            if (isFolder) {
                                String suffix = "";
                                int incrementor = 0;
                                // If other file with same names comes, it would be renamed by adding suffix like (1), (2), etc.
                                do {
                                    try {

                                        Path path = Path.of(root.toFile().getAbsolutePath(), fileItem.getName() + suffix);
                                        localPath = path;
                                        actualName = fileItem.getName() + suffix;
                                        Files.createDirectory(path);
                                        recursiveBackup(userSession, credential, service, fileItem.getId(), path, pageSize, false);
                                        break;
                                    } catch (FileAlreadyExistsException e) {
                                        incrementor++;
                                        suffix = "(" + incrementor + ")";
                                    }
                                } while (true);
                            } else {
                                String suffix = "";
                                int incrementor = 0;
                                // If other file with same names comes, it would be renamed by adding suffix like (1), (2), etc.
                                do {
                                    try {
                                        Path path = Path.of(root.toFile().getAbsolutePath(), fileItem.getName() + suffix);
                                        localPath = path;
                                        actualName = fileItem.getName() + suffix;
                                        Files.createFile(path);
                                        service.files().get(fileItem.getId())
                                                .executeMediaAndDownloadTo(
                                                        new FileOutputStream(path.toFile().getAbsolutePath())
                                                );
                                        break;
                                    } catch (FileAlreadyExistsException e) {
                                        incrementor++;
                                        suffix = "(" + incrementor + ")";
                                    }
                                } while (true);
                            }

                            FileStorage fileStorage = storageService.store(userSession, fileItem, actualName, localPath.toFile().getAbsolutePath()
                                    .replace(dataStorageRootPath.toFile().getAbsolutePath(), ""), shouldLog);

                            if (isFolder) {
                                subscribeChanges(service, credential, fileStorage);
                            }
                        }

                    } catch (IOException ex) {

                        throw new RuntimeException(ex);
                    }
                });
                pageToken = childrenFiles.getNextPageToken();
            } while (pageToken != null);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void connectRootFolder(Credential credential, UserSession userSession, String rootFolder) {


        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("OSlash Drive Connector")
                .build();
        try {
            List<File> rootFolders = this.findFolders(service, rootFolder, dataStorageBatchSize);

            if (rootFolders.isEmpty()) {

                throw new ElementNotFoundException(String.format("No Root Folder %s exists", rootFolder));
            } else if (rootFolders.size() > 1) {

                throw new MultipleElementFoundException(String.format("Multiple Root Folders %s exists", rootFolder));
            } else {
                String rootFolderId = rootFolders.get(0).getId();
                if (this.storageService.getByUserSessionAndDriveId(userSession, rootFolderId).isPresent()) {
                    throw new ElementNotFoundException(String.format("Root Folder %s already connected", rootFolder));
                } else {

                    Optional<FileStorage> existingSubfolder = this.storageService.getByUserSessionAndDriveId(userSession, rootFolderId);
                    FileStorage rootFileStorage = null;
                    if (existingSubfolder.isEmpty()) {
                        Path rootPath = Path.of(dataStorageRootPath.toFile().getAbsolutePath(), rootFolder);
                        Files.createDirectory(rootPath);
                        recursiveBackup(userSession, credential, service, rootFolderId, rootPath, dataStorageBatchSize, false);
                        rootFileStorage = storageService.store(userSession, rootFolders.get(0), rootFolder, rootFolder, false);
                    } else {
                        rootFileStorage = existingSubfolder.get();
                    }

                    subscribeChanges(service, credential, rootFileStorage);
                }
            }

        } catch (IOException ex) {

            throw new RuntimeException(ex);
        }
    }


    public void subscribeChanges(Drive service, Credential credential, FileStorage rootFileStorage) {

        try {
            Channel channel = new Channel()
                    .setId(rootFileStorage.getId() + "+" + UUIDUtility.generate())
                    .setType("web_hook")
                    .setKind("api#channel")
                    .setResourceId(rootFileStorage.getDriveId())
                    .setPayload(true)
                    .setAddress(eventChangeWebhookUri);

            service.files().watch(rootFileStorage.getDriveId(), channel)
                    .setSupportsAllDrives(true)
                    .execute();

        } catch (IOException ex) {

            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean resync(UserSession userSession, Credential credential, List<FileStorage> fileStorages) {

        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("OSlash Drive Connector")
                .build();

        try {

            List<File> rootFolders = this.findFolders(
                    service,
                    fileStorages
            );
            rootFolders.stream().parallel().forEach(rootFolder -> {
                Path path = Path.of(dataStorageRootPath.toFile().getAbsolutePath(), fileStorages.stream().filter(fileStorage -> fileStorage.getDriveId().equals(rootFolder.getId())).findAny().get().getLocalPath());
                recursiveBackup(userSession, credential, service, rootFolder.getId(), path, dataStorageBatchSize, true);
            });
            return true;
        } catch (IOException ex) {

            log.error(String.format("Error in resyncing for the USER ID " + userSession.getUserId(), ex));
            return false;
        }

    }
}
