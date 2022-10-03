package com.oslash.googledrivescrapperplugin.service.googleClient.authorization;

import com.oslash.googledrivescrapperplugin.model.UserTokenDto;

public interface AuthorizationService {

    String generateAuthorizationUrl(String userId);

    UserTokenDto checkAuthorization(String code, String scope, String state);
}
