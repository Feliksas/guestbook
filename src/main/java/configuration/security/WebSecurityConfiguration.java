package configuration.security;


import javax.annotation.sql.DataSourceDefinition;
import javax.sql.DataSource;
import configuration.datasources.AuthDataSourceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SCryptPasswordEncoder sCryptPasswordEncoder;

    private DataSource dataSource;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.
            jdbcAuthentication()
            .usersByUsernameQuery(usersQuery)
            .authoritiesByUsernameQuery(rolesQuery)
            .dataSource(dataSource)
            .passwordEncoder(sCryptPasswordEncoder);

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
                "/error*")
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
    }
}