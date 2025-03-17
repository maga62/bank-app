package com.banking.core.repositories;

import com.banking.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı verileri için repository sınıfı.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Kullanıcı adına göre kullanıcı arar.
     * 
     * @param username Kullanıcı adı
     * @return Kullanıcı
     */
    Optional<User> findByUsername(String username);
    
    /**
     * E-posta adresine göre kullanıcı arar.
     * 
     * @param email E-posta adresi
     * @return Kullanıcı
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Kullanıcı adının var olup olmadığını kontrol eder.
     * 
     * @param username Kullanıcı adı
     * @return Kullanıcı adı varsa true, yoksa false
     */
    boolean existsByUsername(String username);
    
    /**
     * E-posta adresinin var olup olmadığını kontrol eder.
     * 
     * @param email E-posta adresi
     * @return E-posta adresi varsa true, yoksa false
     */
    boolean existsByEmail(String email);
} 