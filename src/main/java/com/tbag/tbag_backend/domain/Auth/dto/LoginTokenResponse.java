package com.tbag.tbag_backend.domain.Auth.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LoginTokenResponse {

  private String accessToken;
  private String refreshToken;

}
