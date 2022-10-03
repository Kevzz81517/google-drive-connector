package com.oslash.googledrivescrapperplugin.service.googleClient.eventStorage;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.Event;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.repository.EventRepository;
import com.oslash.googledrivescrapperplugin.service.eventProcessingService.EventProcessingService;
import com.oslash.googledrivescrapperplugin.service.googleClient.drive.DriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Value("${app.google-services.client.file-event-processing.batch.size:10}")
    private int fileEventProcessingBatchSize;

    @Autowired
    private DriveService driveService;

    @Autowired
    private EventProcessingService eventProcessingService;


    @Override
    public void storeEventAndProcess(String eventId, FileStorage fileStorage, Credential credential) {

        // Check if the Event Already exist, if not, add to the table
        Optional<Event> event = this.eventRepository.findByEventIdAndFileStorage(eventId, fileStorage);
        if (event.isEmpty()) {
            this.eventRepository.save(
                    Event.builder()
                            .eventId(eventId)
                            .fileStorage(fileStorage)
                            .userSessionId(fileStorage.getUserSession().getId())
                            .build()
            );
        }

        long count = this.eventRepository.countByUserSessionId(fileStorage.getUserSession().getId());

        // Check if the event count is greater than equal to Given File Processing Batch size,
        // If yes, execute the Event processing
        if (count >= fileEventProcessingBatchSize) {

            this.eventProcessingService.lockAndProcessEvent(credential, fileStorage, fileEventProcessingBatchSize);
        }

    }

}
