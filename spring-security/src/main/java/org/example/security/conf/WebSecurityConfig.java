package org.example.security.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.example.security.constant.UserRole.*;

/**
 * @author renc
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                // .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(UserPermission.COURSE_WRITE.getPermission())
                // .antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ADMIN.name(), ADMIN_TRAINEE.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin();
    }

    // @Bean
    // AuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider myAuthenticationProvider = new DaoAuthenticationProvider();
    //     myAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    //     return myAuthenticationProvider;
    // }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth.authenticationProvider(authenticationProvider());
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
        UserDetails stuUser = User.builder()
                .username("stu")
                .password(passwordEncoder.encode("stu"))
                // .roles(STUDENT.name())
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails traineeUser = User.builder()
                .username("trainee")
                .password(passwordEncoder.encode("trainee"))
                // .roles(ADMIN_TRAINEE.name())
                .authorities(ADMIN_TRAINEE.getGrantedAuthorities())
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                // .roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                stuUser,
                traineeUser,
                adminUser
        );
    }

    // ========// ========// ========// ========// ========// ========// ========

}
