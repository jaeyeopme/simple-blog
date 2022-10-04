package me.jaeyeop.blog.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jaeyeop.blog.auth.application.port.in.TokenProvideUseCase;
import me.jaeyeop.blog.auth.application.port.out.TokenQueryPort;
import me.jaeyeop.blog.auth.domain.Token;
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

  private final TokenQueryPort tokenQueryPort;

  private final TokenProvideUseCase tokenProvideUseCase;

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

  private UsernamePasswordAuthenticationToken getResult(
      final OAuth2UserPrincipal principal) {
    return new UsernamePasswordAuthenticationToken(principal, Strings.EMPTY,
        principal.getAuthorities());
  }

  private Authentication attemptAuthentication(final HttpServletRequest request) {
    final var token = obtainAccessToken(request);
    final var principal = retrieveUser(token.getEmail());

    return createSuccessAuthentication(request, principal);
  }

  private Token obtainAccessToken(final HttpServletRequest httpServletRequest) {
    final var value = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    final var token = tokenProvideUseCase.authenticate(value);

    if (tokenQueryPort.isExpired(token.getValue())) {
      throw new BadCredentialsException("Bad credentials");
    }

    return token;
  }

  private OAuth2UserPrincipal retrieveUser(final String email) {
    if (!userQueryPort.existsByEmail(email)) {
      throw new UsernameNotFoundException("User not found");
    }

    return OAuth2UserPrincipal.from(userQueryPort.findByEmail(email));
  }

  private Authentication createSuccessAuthentication(
      final HttpServletRequest request,
      final OAuth2UserPrincipal principal) {
    final var result = getResult(principal);
    result.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return result;
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