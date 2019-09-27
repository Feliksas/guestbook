package configuration.security;


import models.auth.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public SCryptPasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(
                "/",
                "/favicon.ico",
                "/jquery/**",
                "/bootstrap/**",
                "/popper/**",
                "/webfonts/**",
                "/fa/**",
                "/error*",
                "/console*",
                "/console/**",
                "/scrypthash",
                "/register")
            .permitAll()
            .regexMatchers("/reviews/[0-9]+").permitAll()
            .antMatchers("/login*").permitAll()
            .anyRequest().hasAnyAuthority(Role.ROLE_USER, Role.ROLE_ADMIN)

            .and()
            .formLogin()
                .loginProcessingUrl("/login")
                .failureHandler(customAuthenticationFailureHandler())
                .defaultSuccessUrl("/")
                .permitAll()

            .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/?logout")
                .deleteCookies("JSESSIONID")

            .and()
            .rememberMe().key("CHANGEME")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400)
            ;
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}