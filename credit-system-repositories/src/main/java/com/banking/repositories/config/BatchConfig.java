package com.banking.repositories.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Configuration class for batch processing.
 * Provides beans for efficient bulk insert and update operations.
 */
@Configuration
public class BatchConfig {
    
    /**
     * JDBC template bean for batch operations.
     * 
     * @param dataSource the data source
     * @return the JDBC template
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(1000);
        return jdbcTemplate;
    }
    
    /**
     * Named parameter JDBC template bean for batch operations with named parameters.
     * 
     * @param dataSource the data source
     * @return the named parameter JDBC template
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
} 