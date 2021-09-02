package org.example.security.support.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toSet;
import static org.example.security.support.security.JwtAuthenticationSuccessHandler.AUTHENTICATION_PREFIX;
import static org.example.security.support.security.JwtAuthenticationSuccessHandler.HMAC256;

/**
 * @author renc
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AuthenticationEntryPoint authenticationEntryPoint = new Http403ForbiddenEntryPoint();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(AUTHENTICATION_PREFIX)) {
            String jwtToken = header.replace(AUTHENTICATION_PREFIX, "");
            if (StringUtils.hasText(jwtToken)) {
                try {
                    try {
                        DecodedJWT decode = JWT.require(HMAC256).build().verify(jwtToken);
                        String username = decode.getSubject();

                        List<String> authorities = decode.getClaim("authorities").asList(String.class);
                        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                                authorities.stream().map(authority -> new SimpleGrantedAuthority(authority)).collect(toSet()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (JWTVerificationException exception){
                        throw new BadCredentialsException("Token is invalid");
                    }
                } catch (AuthenticationException ex) {
                    authenticationEntryPoint.commence(request, response, ex);
                }
            } else {
                authenticationEntryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException("Could not found token"));
            }
        }
        chain.doFilter(request, response);
    }
}
