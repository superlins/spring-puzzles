package org.example.security.support.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author renc
 */
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public static final Algorithm HMAC256 = Algorithm.HMAC256("secret");

    public static final String AUTHENTICATION_PREFIX = "Bearer ";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        List<String> authorities = new ArrayList<>();
        authentication.getAuthorities().forEach(grantedAuthority -> authorities.add(grantedAuthority.getAuthority()));
        String accessToken = JWT.create()
                .withSubject(authentication.getName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withClaim("authorities", authorities)
                .sign(HMAC256);
        String refreshToken = JWT.create()
                .withSubject(authentication.getName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .sign(HMAC256);

        Map<String, String> res = new HashMap<>();
        res.put("access_token", accessToken);
        res.put("refresh_token", accessToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), res);
    }
}
