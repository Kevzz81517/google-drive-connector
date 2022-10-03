package com.oslash.googledrivescrapperplugin.model.entity;


import com.oslash.googledrivescrapperplugin.model.entity.base.BaseAuditableUUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"eventId", "file_storage_id"})})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseAuditableUUID implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_storage_id")
    private FileStorage fileStorage;

    @Type(type = "uuid-char")
    @Column(name = "user_session_id", columnDefinition = "VARCHAR(255)")
    private UUID userSessionId;

    private String eventId;
}
