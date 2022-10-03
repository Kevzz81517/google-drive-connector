package com.oslash.googledrivescrapperplugin.model.entity;

import com.oslash.googledrivescrapperplugin.model.entity.base.BaseAuditableUUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStorage extends BaseAuditableUUID implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    private UserSession userSession;

    private String name;

    private String localName;

    private String localPath;

    private String driveId;

    private String mimeType;

    private Calendar createdTime;

    private boolean isTrashed;

    private String extension;

    private Long size;

    @Column(columnDefinition = "TEXT")
    private String owners;
}
