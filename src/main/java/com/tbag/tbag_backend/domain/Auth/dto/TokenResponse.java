package com.tbag.tbag_backend.domain.Auth.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenResponse {

  private String accessToken;
  private String refreshToken;

}
