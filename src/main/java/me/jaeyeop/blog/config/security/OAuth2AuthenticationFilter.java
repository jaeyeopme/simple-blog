package me.jaeyeop.blog.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaeyeop.blog.auth.application.port.out.ExpiredTokenQueryPort;
import me.jaeyeop.blog.auth.domain.Token;
import me.jaeyeop.blog.config.token.TokenProvider;
import me.jaeyeop.blog.user.application.port.out.UserQueryPort;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFilter extends OncePerRequestFilter {

  private final UserQueryPort userQueryPort;

  private final ExpiredTokenQueryPort expiredTokenQueryPort;

  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain chain)
      throws ServletException, IOException {
    try {
      final var authResult = attemptAuthentication(request);
      successfulAuthentication(authResult);
    } catch (final AuthenticationException e) {
      unsuccessfulAuthentication(e);
    }

    chain.doFilter(request, response);
  }

  private Authentication attemptAuthentication(final HttpServletRequest request) {
    final var acessToken = obtainToken(request);
    final var principal = retrieveUser(acessToken.getEmail());

    return createSuccessAuthentication(request, principal);
  }

  private Token obtainToken(final HttpServletRequest httpServletRequest) {
    final var accessToken = tokenProvider.authenticate(
        httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));

    if (expiredTokenQueryPort.isExpired(accessToken.getValue())) {
      throw new BadCredentialsException("Bad credentials");
    }

    return accessToken;
  }

  private OAuth2UserPrincipal retrieveUser(final String email) {
    final var user = userQueryPort.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return OAuth2UserPrincipal.from(user);
  }

  private Authentication createSuccessAuthentication(
      final HttpServletRequest request,
      final OAuth2UserPrincipal principal) {
    final var result = getResult(principal);
    result.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return result;
  }

  private UsernamePasswordAuthenticationToken getResult(
      final OAuth2UserPrincipal principal) {
    return new UsernamePasswordAuthenticationToken(principal, Strings.EMPTY,
        principal.getAuthorities());
  }

  private void successfulAuthentication(final Authentication authResult) {
    final var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    log.debug("Set SecurityContextHolder to {}", authResult);
  }

  private void unsuccessfulAuthentication(final AuthenticationException failed) {
    SecurityContextHolder.clearContext();
    log.trace("Failed to process authentication request", failed);
    log.trace("Cleared SecurityContextHolder");
    log.trace("Handling authentication failure");
  }

}
