package com.oslash.googledrivescrapperplugin.repository;

import com.oslash.googledrivescrapperplugin.model.entity.EventProcessingLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
public interface EventProcessingLockRepository extends JpaRepository<EventProcessingLock, UUID> {
}
