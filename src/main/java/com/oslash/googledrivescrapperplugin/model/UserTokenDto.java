package com.oslash.googledrivescrapperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record UserTokenDto(String userId, String accessToken, String refreshToken) {
}
