package com.oslash.googledrivescrapperplugin.service.googleClient.credential;

import com.google.api.client.auth.oauth2.Credential;

public interface CredentialService {
    Credential getCredential(String accessToken, String refreshToken);
}
