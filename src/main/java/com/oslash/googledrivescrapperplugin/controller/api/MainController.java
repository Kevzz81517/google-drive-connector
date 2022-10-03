package com.oslash.googledrivescrapperplugin.controller.api;

import com.oslash.googledrivescrapperplugin.service.googleClient.authorization.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private AuthorizationService authorizationService;

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

}
