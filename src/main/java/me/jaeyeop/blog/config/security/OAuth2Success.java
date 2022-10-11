package me.jaeyeop.blog.config.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class OAuth2Success {

  private final String accessToken;

  private final String refreshToken;

  public OAuth2Success(final String accessToken, final String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

}
