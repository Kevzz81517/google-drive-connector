package com.oslash.googledrivescrapperplugin.model.entity;

import com.oslash.googledrivescrapperplugin.model.entity.base.BaseAuditableUUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession extends BaseAuditableUUID implements Serializable {

    private static String ROOT_FOLDERS_SPLIT_TOKEN = "&!%#2";

    @Column(length = 320, unique = true, nullable = false)
    private String userId;

    private String accessToken;

    private String refreshToken;
}
