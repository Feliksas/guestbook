package configuration.security;


import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public SCryptPasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder();
    }

    @Autowired
    private DataSource authDataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.
            jdbcAuthentication()
            .usersByUsernameQuery("SELECT USERNAME,PASSWORD FROM USER WHERE USERNAME=?")
            .authoritiesByUsernameQuery("SELECT r.ROLE FROM (SELECT USER_ID FROM USER WHERE USERNAME=?) as u INNER JOIN USER_ROLE ur ON ur.USER_ID = u.USER_ID INNER JOIN ROLE r ON r.ROLE_ID = ur.ROLE_ID")
            .dataSource(authDataSource)
            .passwordEncoder(passwordEncoder());

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
                "/error*",
                "/console*",
                "/console/**")
            .permitAll()
            .regexMatchers("/reviews/[0-9]+").permitAll()
            .antMatchers("/login*").permitAll()
            .anyRequest().authenticated()

            .and()
            .formLogin()
                .loginProcessingUrl("/login")
                .failureUrl("/?loginerror")
                .defaultSuccessUrl("/")
                .permitAll()

            .and()
            .logout()
                .permitAll()
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