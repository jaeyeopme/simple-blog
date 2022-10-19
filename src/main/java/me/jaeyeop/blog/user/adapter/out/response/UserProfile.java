package me.jaeyeop.blog.user.adapter.out.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import me.jaeyeop.blog.config.security.OAuth2Provider;
import me.jaeyeop.blog.user.domain.User;

@EqualsAndHashCode
public class UserProfile {

  private final String email;

  private final String name;

  private final String picture;

  private final OAuth2Provider provider;

  @Builder(access = AccessLevel.PRIVATE)
  private UserProfile(final String email,
      final String name,
      final String picture,
      final OAuth2Provider provider) {
    this.email = email;
    this.name = name;
    this.picture = picture;
    this.provider = provider;
  }

  public static UserProfile from(final User user) {
    return UserProfile.builder()
        .email(user.getEmail())
        .name(user.getName())
        .picture(user.getPicture())
        .provider(user.getProvider())
        .build();
  }

}