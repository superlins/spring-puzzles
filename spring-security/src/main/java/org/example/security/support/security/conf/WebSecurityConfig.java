package org.example.security.support.security.conf;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.example.security.repo.UserRepository;
import org.example.security.support.security.CaptchaProperties;
import org.example.security.support.security.DefaultFilterInvocationSecurityMetadataSource;
import org.example.security.support.security.JwtAuthenticationFilter;
import org.example.security.support.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
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

import java.util.List;
import java.util.Properties;

/**
 * @author renc
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccessDecisionManager accessDecisionManager;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    public WebSecurityConfig(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             AccessDecisionManager accessDecisionManager,
                             JwtAuthenticationFilter jwtAuthenticationFilter,
                             FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessDecisionManager = accessDecisionManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
                // .sessionManagement()
                // .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // .and()
                // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                // .antMatchers("/static/*").permitAll()
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
                .and()
                .formLogin()
                // .loginProcessingUrl("")
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
                // .and()
                // .logout()
                // .addLogoutHandler(null)
                // .logoutSuccessHandler(null)
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
        // UserDetailsService userDetailsService = userDetailsService();
        // DaoAuthenticationProvider provider = new CaptchaDaoAuthenticationProvider();
        // provider.setUserDetailsService(userDetailsService);
        // provider.setPasswordEncoder(passwordEncoder);
        // provider.afterPropertiesSet();
        // auth.authenticationProvider(provider);
        super.configure(auth);
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

    //----////----////----////----////----////----////----////----//

    @Configuration(proxyBeanMethods = false)
    static class NestedWebSecurityConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(10);
        }

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
    }

    @Configuration(proxyBeanMethods = false)
    static class CaptchaSecurityConfig {

        // TODO(renc): inject this
        private CaptchaProperties captchaProperties;

        @Bean
        Producer producer() {
            Properties properties = new Properties();
            properties.setProperty("kaptcha.image.width", "150");
            properties.setProperty("kaptcha.image.height", "50");
            properties.setProperty("kaptcha.textproducer.char.string", "0123456789");
            properties.setProperty("kaptcha.textproducer.char.length", "4");
            Config config = new Config(properties);
            DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
            defaultKaptcha.setConfig(config);
            return defaultKaptcha;
        }
    }
}
