// package org.example.keycloak.conf.security;
//
// import org.keycloak.adapters.KeycloakConfigResolver;
// import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
// import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
// import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
// import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
// import org.springframework.security.core.session.SessionRegistryImpl;
// import org.springframework.security.web.authentication.logout.LogoutFilter;
// import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
// import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
// import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
// import org.springframework.security.web.session.HttpSessionEventPublisher;
//
// /**
//  * @author renc
//  * @see org.springframework.security.config.annotation.web.builders.FilterOrderRegistration
//  */
// @Configuration
// public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
//
//     @Bean
//     public KeycloakConfigResolver keycloakConfigResolver() {
//         return new KeycloakSpringBootConfigResolver();
//     }
//
//     @Bean
//     public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
//         return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
//     }
//
//     @Override
//     protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
//         return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
//     }
//
//     @Override
//     protected void configure(HttpSecurity http) throws Exception {
//         http
//                 .csrf().requireCsrfProtectionMatcher(keycloakCsrfRequestMatcher())
//                 // .and()
//                 // .sessionManagement()
//                 // .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
//                 .and()
//                 .authorizeRequests()
//                 // .antMatchers("/customer").hasRole("base")
//                 .anyRequest().permitAll()
//                 .and()
//                 .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
//                 .addFilterBefore(keycloakAuthenticationProcessingFilter(), LogoutFilter.class)
//                 .addFilterAfter(keycloakSecurityContextRequestFilter(), SecurityContextHolderAwareRequestFilter.class)
//                 .addFilterAfter(keycloakAuthenticatedActionsRequestFilter(), SecurityContextHolderAwareRequestFilter.class)
//                 .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
//                 .and()
//                 .logout()
//                 .addLogoutHandler(keycloakLogoutHandler())
//                 .logoutUrl("/sso/logout").permitAll()
//                 .logoutSuccessUrl("/");
//     }
//
//     @Override
//     protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//         KeycloakAuthenticationProvider authenticationProvider = super.keycloakAuthenticationProvider();
//         authenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
//         auth.authenticationProvider(authenticationProvider);
//     }
// }
