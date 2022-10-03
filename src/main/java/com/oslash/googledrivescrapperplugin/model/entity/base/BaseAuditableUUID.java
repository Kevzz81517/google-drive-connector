package com.oslash.googledrivescrapperplugin.model.entity.base;

import com.oslash.googledrivescrapperplugin.util.UUIDUtility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@AllArgsConstructor
public class BaseAuditableUUID extends BaseAuditable implements Serializable {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "VARCHAR(255)")
    private UUID id;

    public BaseAuditableUUID() {
        this.id = UUIDUtility.generate();
    }

}
