package com.banking.repositories.abstracts;

import com.banking.entities.SuspiciousTransaction;
import com.banking.entities.enums.RiskLevel;
import com.banking.entities.enums.TransactionStatus;
import com.banking.entities.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Şüpheli işlemler için repository sınıfı.
 */
@Repository
public interface SuspiciousTransactionRepository extends JpaRepository<SuspiciousTransaction, Long> {
    
    /**
     * Müşteri ID'sine göre şüpheli işlemleri bulur.
     * 
     * @param customerId Müşteri ID
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByCustomerId(Long customerId);
    
    /**
     * Müşteri ID'sine göre şüpheli işlemleri sayfalı olarak bulur.
     * 
     * @param customerId Müşteri ID
     * @param pageable Sayfalama bilgisi
     * @return Şüpheli işlem sayfası
     */
    Page<SuspiciousTransaction> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Risk seviyesine göre şüpheli işlemleri bulur.
     * 
     * @param riskLevel Risk seviyesi
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByRiskLevel(RiskLevel riskLevel);
    
    /**
     * Duruma göre şüpheli işlemleri bulur.
     * 
     * @param status Durum
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByStatus(TransactionStatus status);
    
    /**
     * İşlem türüne göre şüpheli işlemleri bulur.
     * 
     * @param transactionType İşlem türü
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByTransactionType(TransactionType transactionType);
    
    /**
     * Belirli bir tarih aralığında tespit edilen şüpheli işlemleri bulur.
     * 
     * @param startDate Başlangıç tarihi
     * @param endDate Bitiş tarihi
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByDetectionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Belirli bir risk skorundan yüksek şüpheli işlemleri bulur.
     * 
     * @param riskScore Risk skoru
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByRiskScoreGreaterThanEqual(Integer riskScore);
    
    /**
     * Belirli bir müşterinin belirli bir risk seviyesindeki şüpheli işlemlerini bulur.
     * 
     * @param customerId Müşteri ID
     * @param riskLevel Risk seviyesi
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByCustomerIdAndRiskLevel(Long customerId, RiskLevel riskLevel);
    
    /**
     * Belirli bir müşterinin belirli bir durumdaki şüpheli işlemlerini bulur.
     * 
     * @param customerId Müşteri ID
     * @param status Durum
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByCustomerIdAndStatus(Long customerId, TransactionStatus status);
    
    /**
     * Belirli bir IP adresinden yapılan şüpheli işlemleri bulur.
     * 
     * @param ipAddress IP adresi
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByIpAddress(String ipAddress);
    
    /**
     * Son 24 saat içinde tespit edilen şüpheli işlemleri bulur.
     * 
     * @param now Şu anki zaman
     * @return Şüpheli işlem listesi
     */
    @Query("SELECT s FROM SuspiciousTransaction s WHERE s.detectionDate >= :now - 1")
    List<SuspiciousTransaction> findDetectedInLast24Hours(@Param("now") LocalDateTime now);
    
    /**
     * Belirli bir müşterinin son şüpheli işlemini bulur.
     * 
     * @param customerId Müşteri ID
     * @return Şüpheli işlem listesi
     */
    @Query("SELECT s FROM SuspiciousTransaction s WHERE s.customer.id = :customerId ORDER BY s.detectionDate DESC")
    List<SuspiciousTransaction> findLatestByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    /**
     * Belirli bir müşterinin belirli bir işlem türündeki ve belirli bir tarihten sonraki şüpheli işlemlerini bulur.
     * 
     * @param customerId Müşteri ID
     * @param transactionType İşlem türü
     * @param startTime Başlangıç zamanı
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByCustomerIdAndTransactionTypeAndTransactionDateAfter(
            Long customerId, TransactionType transactionType, LocalDateTime startTime);
} 