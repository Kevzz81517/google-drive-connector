package com.oslash.googledrivescrapperplugin.service.googleClient.authorization;

import com.oslash.googledrivescrapperplugin.configuration.GoogleApiConfiguration;
import com.oslash.googledrivescrapperplugin.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
}
