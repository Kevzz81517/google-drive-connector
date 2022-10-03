package com.oslash.googledrivescrapperplugin.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oslash.googledrivescrapperplugin.exception.MalformedDataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${app.google-services.client.authorization.state.secret-key}")
    private String secret;

    public String encode(Map<String, String> claims) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create()
                .withIssuer("oslash");
        claims.forEach(builder::withClaim);
        return builder.sign(algorithm);
    }


    public Map<String, String> decodeAllStringClaims(String token) throws MalformedDataException {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("oslash")
                    .build();
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getClaims()
                    .entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().as(String.class)));

        } catch (JWTVerificationException exception) {

            throw new MalformedDataException("Data is Malformed");
        }
    }


}
