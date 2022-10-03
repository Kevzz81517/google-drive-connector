package com.oslash.googledrivescrapperplugin.model.entity;

import com.oslash.googledrivescrapperplugin.model.entity.base.BaseAuditable;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventProcessingLock extends BaseAuditable implements Serializable {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "VARCHAR(255)")
    private UUID id;
}
