package org.example.security.support.security;

import org.example.security.constant.UserPermission;
import org.example.security.constant.UserRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class DefaultFilterInvocationSecurityMetadataSource
        implements FilterInvocationSecurityMetadataSource, InitializingBean {

    private Map<RequestMatcher, Collection<ConfigAttribute>> matchers = new HashMap<>();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        return matchers.entrySet().stream()
                .filter(e -> e.getKey().matches(request))
                .findFirst()
                .map(e -> e.getValue())
                .orElse(null);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.matchers.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (UserRole userRole : UserRole.values()) {
            for (UserPermission userPermission : userRole.getPermissions()) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(
                        userPermission.getUri(), userPermission.getMethod());
                matchers.computeIfAbsent(matcher, m -> new ArrayList<>())
                        .add(new SecurityConfig("ROLE_" + userRole.name()));
            }
        }
    }
}