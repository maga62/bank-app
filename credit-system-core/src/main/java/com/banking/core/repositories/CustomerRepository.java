package com.banking.core.repositories;

import com.banking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Müşteri verileri için repository sınıfı.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Müşteri adına göre müşteri arar.
     * 
     * @param firstName Ad
     * @param lastName Soyad
     * @return Müşteri listesi
     */
    Customer findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Kimlik numarasına göre müşteri arar.
     * 
     * @param identityNumber Kimlik numarası
     * @return Müşteri
     */
    Customer findByIdentityNumber(String identityNumber);
    
    /**
     * E-posta adresine göre müşteri arar.
     * 
     * @param email E-posta adresi
     * @return Müşteri
     */
    Customer findByEmail(String email);
    
    /**
     * Telefon numarasına göre müşteri arar.
     * 
     * @param phoneNumber Telefon numarası
     * @return Müşteri
     */
    Customer findByPhoneNumber(String phoneNumber);
} 