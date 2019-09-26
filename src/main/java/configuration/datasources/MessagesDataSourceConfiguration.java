package configuration.datasources;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import models.messages.GuestBookEntry;
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
@EnableJpaRepositories(basePackages = "repository.messages",
    entityManagerFactoryRef = "messagesEntityManagerFactory",
    transactionManagerRef = "messagesTransactionManager")

public class MessagesDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("guestbook.datasource.messages")
    public DataSourceProperties messagesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource messagesDataSource() {
        return messagesDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean
    messagesEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(messagesDataSource())
            .packages(GuestBookEntry.class)
            .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager messagesTransactionManager(
        final @Qualifier("messagesEntityManagerFactory") LocalContainerEntityManagerFactoryBean
            messagesEntityManagerFactory) {
        return new JpaTransactionManager(messagesEntityManagerFactory.getObject());
    }
}
