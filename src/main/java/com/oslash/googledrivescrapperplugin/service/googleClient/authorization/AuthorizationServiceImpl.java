package com.oslash.googledrivescrapperplugin.service.googleClient.authorization;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.oslash.googledrivescrapperplugin.configuration.GoogleApiConfiguration;
import com.oslash.googledrivescrapperplugin.exception.InvalidScopeAcceptedException;
import com.oslash.googledrivescrapperplugin.model.UserTokenDto;
import com.oslash.googledrivescrapperplugin.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private GoogleApiConfiguration googleApiConfiguration;

    @Autowired
    private JwtService jwtService;

    @Value("#{'${app.google-services.client.auth.code-redirection-host}' + '${app.google-services.client.auth.code-redirection-uri}'}")
    private String codeRedirectionUri;

    @Override
    public String generateAuthorizationUrl(String userId) {

        return googleApiConfiguration.googleAuthorizationCodeFlow().newAuthorizationUrl()
                .setState(jwtService.encode(Collections.singletonMap("userId", userId)))
                .setRedirectUri(codeRedirectionUri)
                .toString();
    }

    private GoogleTokenResponse getAccessTokenFromCode(String code) {

        try {
            return googleApiConfiguration.googleAuthorizationCodeFlow()
                    .newTokenRequest(code)
                    .setRedirectUri(codeRedirectionUri)
                    .execute();
        } catch (IOException ex) {

            throw new RuntimeException(ex);
        }
    }

    @Override
    public UserTokenDto checkAuthorization(String code, String scope, String state) {

        if (scope.split(",").length != googleApiConfiguration.getScopes().size()) {
            throw new InvalidScopeAcceptedException("Please initiate again and accept all the scopes");
        } else {

            var claims = this.jwtService.decodeAllStringClaims(state);

            String userId = claims.get("userId");

            GoogleTokenResponse token = getAccessTokenFromCode(code);

            return new UserTokenDto(userId, token.getAccessToken(), token.getRefreshToken());
        }
    }
}
