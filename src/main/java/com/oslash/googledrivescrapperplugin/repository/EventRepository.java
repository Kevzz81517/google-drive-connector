package com.oslash.googledrivescrapperplugin.repository;

import com.oslash.googledrivescrapperplugin.model.entity.Event;
import com.oslash.googledrivescrapperplugin.model.entity.FileStorage;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Optional<Event> findByEventIdAndFileStorage(String eventId, FileStorage fileStorage);

    long countByUserSessionId(UUID id);

    @Query("SELECT fileStorage as fileStorage, max(e.createdOn) as maxCreatedOn from Event e JOIN e.fileStorage fileStorage WHERE fileStorage.userSession = ?1 ORDER BY maxCreatedOn")
    Page<DistinctEventMapper> findDistinctByFileStorage_UserSessionOrderByCreatedOn(UserSession userSession, Pageable page);

    @Modifying
    @Transactional
    @Query("DELETE FROM Event e WHERE e.userSessionId = ?1 AND e.createdOn <= ?2")
    int deleteByUserSessionIdAndCreatedOnLessThanEqualTo(UUID id, Calendar createdOn);

    interface DistinctEventMapper {

        FileStorage getFileStorage();

        Calendar getMaxCreatedOn();

    }
}
