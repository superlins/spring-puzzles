package org.example.security.support.security.conf;

import org.example.security.repo.UserRepository;
import org.example.security.support.security.KaptchaDaoAuthenticationProvider;
import org.example.security.support.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * @author renc
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    private final AccessDecisionManager accessDecisionManager;

    private final FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    public WebSecurityConfig(UserRepository userRepository,
                             AccessDecisionManager accessDecisionManager,
                             FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
        this.userRepository = userRepository;
        this.accessDecisionManager = accessDecisionManager;
        this.filterInvocationSecurityMetadataSource = filterInvocationSecurityMetadataSource;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/static/*", "/css/*", "/js/*").permitAll()
                // .antMatchers("/api/**").hasRole(STUDENT.name())
                // .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMIN_TRAINEE.name())
                .anyRequest()
                .authenticated()
                .withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor()) // FilterSecurityInterceptor replace
                // .and()
                // .rememberMe()
                // .tokenRepository(null)
                // .tokenValiditySeconds(1)
                .and()
                .formLogin()
                // .successHandler((req, resp, auth) -> {
                //     resp.setContentType("application/json;charset=utf-8");
                //     PrintWriter out = resp.getWriter();
                //     out.write(new ObjectMapper().writeValueAsString(RespBean.ok("success", auth.getPrincipal())));
                //     out.flush();
                //     out.close();
                // })
                // .failureHandler((req, resp, e) -> {
                //     resp.setContentType("application/json;charset=utf-8");
                //     PrintWriter out = resp.getWriter();
                //     out.write(new ObjectMapper().writeValueAsString(RespBean.error(e.getMessage())));
                //     out.flush();
                //     out.close();
                // })
        // .loginPage("")
        // .loginProcessingUrl("")
        ;
    }

    private ObjectPostProcessor<FilterSecurityInterceptor> filterSecurityInterceptorObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                object.setAccessDecisionManager(accessDecisionManager);
                object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                return object;
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetailsService userDetailsService = userDetailsService();
        PasswordEncoder passwordEncoder = passwordEncoder();
        DaoAuthenticationProvider provider = new KaptchaDaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.afterPropertiesSet();
        auth.authenticationProvider(provider);
    }

    /**
     * User-Role Mappings
     *
     * @see org.springframework.security.core.GrantedAuthority
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
