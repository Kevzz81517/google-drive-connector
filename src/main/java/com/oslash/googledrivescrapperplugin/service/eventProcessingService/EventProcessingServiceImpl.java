package com.oslash.googledrivescrapperplugin.service.eventProcessingService;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.entity.EventProcessingLock;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import com.oslash.googledrivescrapperplugin.repository.EventProcessingLockRepository;
import com.oslash.googledrivescrapperplugin.repository.EventRepository;
import com.oslash.googledrivescrapperplugin.service.googleClient.drive.DriveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventProcessingServiceImpl implements EventProcessingService {


    @Autowired
    private EventProcessingLockRepository eventProcessingLockRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DriveService driveService;

    @Value("${app.google-services.client.file-event-processing.lock.time-period}")
    private long eventProcessingLockTimePeriod;

    @Async
    @Override
    public void lockAndProcessEvent(Credential credential, FileStorage fileStorage, int fileEventProcessingBatchSize) {

        // Try to get lock
        try {
            this.eventProcessingLockRepository.save(
                    EventProcessingLock.builder()
                            .id(fileStorage.getUserSession().getId())
                            .build()
            );

            log.info("Event Processing Lock acquired for USER ID " + fileStorage.getUserSession().getUserId());

            this.processEvent(fileStorage.getUserSession(), credential, fileEventProcessingBatchSize);

            // Once the processing is done, remove the acquired lock

            this.eventProcessingLockRepository.deleteById(fileStorage.getUserSession().getId());
        } catch (DataIntegrityViolationException ex) {

            // If lock fails check if the acquired lock has lasted longer than the allowed time
            // Handling the unhandleable condition when lock couldn't be removed

            var lock = this.eventProcessingLockRepository.findById(fileStorage.getUserSession().getId());

            if (lock.isPresent() && Calendar.getInstance().getTimeInMillis() - lock.get().getCreatedOn().getTimeInMillis() >= eventProcessingLockTimePeriod) {
                this.eventProcessingLockRepository.deleteById(fileStorage.getUserSession().getId());
                lock = Optional.empty();
            }

            if (lock.isEmpty()) {

                try {
                    this.eventProcessingLockRepository.save(
                            EventProcessingLock.builder()
                                    .id(fileStorage.getUserSession().getId())
                                    .build()
                    );

                    log.info("Event Processing Lock acquired for USER ID " + fileStorage.getUserSession().getUserId());

                    this.processEvent(fileStorage.getUserSession(), credential, fileEventProcessingBatchSize);

                    log.info("Event Processing Completed for USER ID " + fileStorage.getUserSession().getUserId());

                } catch (DataIntegrityViolationException e) {

                    log.info("Event Processing Lock couldn't be acquired for USER ID " + fileStorage.getUserSession().getUserId());
                } finally {

                    // Once the processing is done, remove the acquired lock
                    this.eventProcessingLockRepository.deleteById(fileStorage.getUserSession().getId());
                }
            }
        } finally {

            this.eventProcessingLockRepository.deleteById(fileStorage.getUserSession().getId());
        }
    }

    public void processEvent(UserSession userSession, Credential credential, int fileEventProcessingBatchSize) {

        Page<EventRepository.DistinctEventMapper> events = this.eventRepository.findDistinctByFileStorage_UserSessionOrderByCreatedOn(userSession, PageRequest.of(0, fileEventProcessingBatchSize));

        int pageNo = 0;

        do {
            try {
                boolean resyncCompleted = this.driveService.resync(
                        userSession, credential, events.stream()
                                .map(EventRepository.DistinctEventMapper::getFileStorage)
                                .collect(Collectors.toList())
                );
                if (resyncCompleted) {

                    // Remove the events once all the events are synced with local
                    this.eventRepository.deleteByUserSessionIdAndCreatedOnLessThanEqualTo(
                            userSession.getId(),
                            events.getContent().get(events.getContent().size() - 1).getMaxCreatedOn()
                    );
                }
            } catch (Throwable ex) {

                log.error("Event Processing Failed for USER ID " + userSession.getId(), ex);

                // Skip the page, if processing fails
                pageNo++;
            }

            events = this.eventRepository.findDistinctByFileStorage_UserSessionOrderByCreatedOn(
                    userSession,
                    PageRequest.of(pageNo, fileEventProcessingBatchSize)
            );
        } while (events.hasNext() && events.getSize() == fileEventProcessingBatchSize);
    }
}
