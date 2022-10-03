package com.oslash.googledrivescrapperplugin.service.user;

import com.oslash.googledrivescrapperplugin.model.entity.UserSession;

public interface UserSessionService {
    UserSession upsert(String userId, String accessToken, String refreshToken);

    UserSession get(String userId);
}
