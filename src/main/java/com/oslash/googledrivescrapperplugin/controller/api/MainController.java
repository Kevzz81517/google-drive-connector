package com.oslash.googledrivescrapperplugin.controller.api;

import com.google.api.client.auth.oauth2.Credential;
import com.oslash.googledrivescrapperplugin.model.UserTokenDto;
import com.oslash.googledrivescrapperplugin.model.entity.UserSession;
import com.oslash.googledrivescrapperplugin.model.request.ConnectNewRootFolderRequest;
import com.oslash.googledrivescrapperplugin.service.googleClient.authorization.AuthorizationService;
import com.oslash.googledrivescrapperplugin.service.googleClient.credential.CredentialService;
import com.oslash.googledrivescrapperplugin.service.googleClient.drive.DriveService;
import com.oslash.googledrivescrapperplugin.service.storage.StorageService;
import com.oslash.googledrivescrapperplugin.service.user.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private DriveService driveService;

    @Autowired
    private StorageService storageService;

    /**
     * Generated the authorization link where user can authorize application to use Google Apis
     *
     * @param userId Unique User Id, Identifier used for other operations to identify the user
     * @return
     */
    @GetMapping("/authorization/initiate/{userId}")
    public ResponseEntity initiateAuthorization(@PathVariable String userId) {

        String authorizationUrl = this.authorizationService.generateAuthorizationUrl(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION.toString(), authorizationUrl);
        return new ResponseEntity<String>(headers, HttpStatus.SEE_OTHER);
    }

    /**
     * Google will redirect user to this redirection link
     *
     * @param code  Provided by Google to get the Token
     * @param scope Scopes authorized by the user
     * @param state JWT encoded details required to uniquely identify the user (Sent with the link)
     */
    @GetMapping("${app.google-services.client.auth.code-redirection-uri}")
    public void verifyAuthorization(@RequestParam String code, @RequestParam String scope, @RequestParam String state) {


        UserTokenDto userTokens = this.authorizationService.checkAuthorization(code, scope, state);
        this.userSessionService.upsert(userTokens.userId(), userTokens.accessToken(), userTokens.refreshToken());
    }

    /**
     * @param connectNewRootFolderRequest userId
     *                                    Uniquely identify the user
     *                                    rootFolder
     *                                    Name of the Root folder to be connected to the connector
     *                                    <p>
     *                                    Returns 200 OK if the Folder gets connected successfully
     *                                    Returns 409 CONFLICT if the Folder is already connected
     *                                    Returns 409 CONFLICT if multiple folders with the same name exists
     */
    @PostMapping("/connect/user/root")
    public void connectFolder(@RequestBody ConnectNewRootFolderRequest connectNewRootFolderRequest) {

        UserSession userSession = this.userSessionService.get(connectNewRootFolderRequest.userId());
        Credential credential = this.credentialService.getCredential(userSession.getAccessToken(), userSession.getRefreshToken());
        this.driveService.connectRootFolder(credential, userSession, connectNewRootFolderRequest.rootFolder());
    }

}
