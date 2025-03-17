package com.banking.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Repository yapılandırması.
 * Bu sınıf, core modülünün repositories modülündeki repository sınıflarını kullanabilmesini sağlar.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.banking.repositories.abstracts"})
@ComponentScan(basePackages = {"com.banking.repositories"})
public class RepositoryConfig {
    // Repository yapılandırması için gerekli konfigürasyonlar burada tanımlanabilir
} 