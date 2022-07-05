package com.tfg.restapi.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseConfig {
    /**
     * Configuración de JDBC
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {

        DataSourceProperties dbprops = new DataSourceProperties();
/*
        dbprops.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dbprops.setUsername("postgres");
        dbprops.setPassword("tfgcarmen");
        dbprops.setDriverClassName("org.postgresql.Driver");

 */


        dbprops.setUrl("jdbc:postgresql://virtual-tei.postgres.database.azure.com:5432/postgres");
        dbprops.setUsername("virtualTei@virtual-tei");
        dbprops.setPassword("@tfgCarmen2022");
        dbprops.setDriverClassName("org.postgresql.Driver");

        return dbprops;


    }

    /**
     * Data source de JDBC
     * @param properties
     * @return
     */
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
