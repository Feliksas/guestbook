package configuration.security;


import domain.auth.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import service.UserService;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
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
                "/css/**",
                "/js/**",
                "/error*",
                "/console/**",
                "/scrypthash",
                "/add",
                "/register")
            .permitAll()
            .regexMatchers("/reviews/[0-9]+").permitAll()
            .antMatchers("/login*").permitAll()
            .antMatchers("/admin/**").hasAuthority(Role.ROLE_ADMIN.getRole())
            .anyRequest().hasAnyAuthority(Role.ROLE_USER.getRole(), Role.ROLE_ADMIN.getRole())

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
            .userDetailsService(userService)
            .tokenValiditySeconds(86400)
        ;
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}