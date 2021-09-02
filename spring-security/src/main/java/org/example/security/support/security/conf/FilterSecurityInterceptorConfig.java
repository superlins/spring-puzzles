package org.example.security.support.security.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author renc
 */
@Configuration(proxyBeanMethods = false)
public class FilterSecurityInterceptorConfig {

    @Bean
    FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource() {
        return new DefaultFilterInvocationSecurityMetadataSource();
    }

    @Bean
    AccessDecisionManager accessDecisionManager(List<AccessDecisionVoter<?>> accessDecisionVoters) {
        return new AffirmativeBased(accessDecisionVoters);
    }

    @Bean
    public RoleVoter roleVoter() {
        return new RoleVoter();
    }

    static class DefaultFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

        private Map<RequestMatcher, Collection<ConfigAttribute>> matchers; // TODO loads from db

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
    }
}
