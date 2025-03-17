package com.banking.repositories.abstracts;

import com.banking.entities.Payment;
import com.banking.entities.enums.PaymentProvider;
import com.banking.entities.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findByCustomerId(Long customerId);
    
    List<Payment> findByApplicationId(Long applicationId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByProvider(PaymentProvider provider);
} 