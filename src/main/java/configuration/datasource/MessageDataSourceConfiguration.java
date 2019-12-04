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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "repository.message",
    entityManagerFactoryRef = "messageEntityManagerFactory",
    transactionManagerRef = "messageTransactionManager")
public class MessageDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("guestbook.datasource.message")
    public DataSourceProperties messageDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "messageDataSource")
    @Primary
    public DataSource messageDataSource() {
        return messageDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean
    messageEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(messageDataSource())
            .packages("domain.message")
            .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager messageTransactionManager(
        final @Qualifier("messageEntityManagerFactory") LocalContainerEntityManagerFactoryBean
            messageEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(messageEntityManagerFactory.getObject()));
    }
}
