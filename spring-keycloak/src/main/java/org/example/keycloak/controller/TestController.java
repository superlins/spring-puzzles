package org.example.keycloak.controller;

import com.oceanum.adapters.keycloak.IDProvider;
import com.oceanum.adapters.keycloak.idm.HierarchicalResourceRepresentation;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * @author renc
 */
@RestController
public class TestController {

    @GetMapping("/dashboard")
    public ResponseEntity<Object> focus(HttpServletRequest request) {
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> keycloakPrincipal = (KeycloakPrincipal) request.getUserPrincipal();

        System.out.println(">>> REQUEST-HEADERS <<<");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }
        return ResponseEntity.ok("DASHBOARD");
    }

    @GetMapping("/anonymous")
    public Object anonymous() {
        return "anonymous resource";
    }
}
