package com.banking.repositories.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for read replica database.
 * Provides routing data source for read/write operations.
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class ReadReplicaConfig {

    /**
     * Primary data source properties.
     *
     * @return the data source properties
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Replica data source properties.
     *
     * @return the data source properties
     */
    @Bean
    @ConfigurationProperties("spring.datasource.replica")
    public DataSourceProperties replicaDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Primary data source.
     *
     * @return the data source
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary.hikari")
    public DataSource primaryDataSource() {
        HikariDataSource dataSource = primaryDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("primary-pool");
        return dataSource;
    }

    /**
     * Replica data source.
     *
     * @return the data source
     */
    @Bean
    @ConfigurationProperties("spring.datasource.replica.hikari")
    public DataSource replicaDataSource() {
        HikariDataSource dataSource = replicaDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("replica-pool");
        dataSource.setReadOnly(true);
        return dataSource;
    }

    /**
     * Routing data source.
     *
     * @param primaryDataSource the primary data source
     * @param replicaDataSource the replica data source
     * @return the routing data source
     */
    @Bean
    public DataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            @Qualifier("replicaDataSource") DataSource replicaDataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(DataSourceType.PRIMARY, primaryDataSource);
        dataSources.put(DataSourceType.REPLICA, replicaDataSource);

        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource);

        return routingDataSource;
    }

    /**
     * Lazy connection data source proxy.
     *
     * @param routingDataSource the routing data source
     * @return the lazy connection data source proxy
     */
    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    /**
     * JDBC template for replica data source.
     *
     * @param replicaDataSource the replica data source
     * @return the JDBC template
     */
    @Bean
    public JdbcTemplate replicaJdbcTemplate(@Qualifier("replicaDataSource") DataSource replicaDataSource) {
        return new JdbcTemplate(replicaDataSource);
    }

    /**
     * Enum for data source types.
     */
    public enum DataSourceType {
        /**
         * Primary data source.
         */
        PRIMARY,
        
        /**
         * Replica data source.
         */
        REPLICA
    }
} 