package com.oslash.googledrivescrapperplugin.service.user;

import com.oslash.googledrivescrapperplugin.exception.ElementNotFoundException;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import com.oslash.googledrivescrapperplugin.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public UserSession upsert(String userId, String accessToken, String refreshToken) {

        Optional<UserSession> optionalUserSession = this.userSessionRepository.findByUserId(userId);

        UserSession userSession = null;

        userSession = optionalUserSession.orElseGet(() -> UserSession.builder().userId(userId).build());

        userSession.setAccessToken(accessToken);
        userSession.setRefreshToken(refreshToken);

        return this.userSessionRepository.save(userSession);
    }

    @Override
    public UserSession get(String userId) {

        Optional<UserSession> userSession = this.userSessionRepository.findByUserId(userId);
        if (userSession.isPresent()) {

            return userSession.get();
        } else {

            throw new ElementNotFoundException(String.format("User with ID %s not authorized", userId));
        }
    }
}
