package configuration.datasource;

import javax.sql.DataSource;
import java.util.Objects;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "repository.auth",
    entityManagerFactoryRef = "authEntityManagerFactory",
    transactionManagerRef = "authTransactionManager")
public class AuthDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("guestbook.datasource.auth")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "authDataSource")
    public DataSource authDataSource() {
        return authDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean
    authEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(authDataSource())
            .packages("domain.auth")
            .build();
    }

    @Bean
    public PlatformTransactionManager authTransactionManager(
        final @Qualifier("authEntityManagerFactory") LocalContainerEntityManagerFactoryBean
            authEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(authEntityManagerFactory.getObject()));
    }
}
