package com.oslash.googledrivescrapperplugin.service.googleClient.credential;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialServiceImpl implements CredentialService {

    @Autowired
    private HttpTransport httpTransport;

    @Autowired
    private JsonFactory jsonFactory;

    @Autowired
    private GoogleClientSecrets clientSecrets;

    @Override
    public Credential getCredential(String accessToken, String refreshToken) {

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setTokenServerUrl(new GenericUrl(clientSecrets.getDetails().getTokenUri()))
                .setClientAuthentication(
                        new ClientParametersAuthentication(
                                clientSecrets.getDetails().getClientId(),
                                clientSecrets.getDetails().getClientSecret()
                        )
                )
                .setRequestInitializer(httpRequest -> {
                })
                .setClock(Clock.SYSTEM)
                .build()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
    }
}
