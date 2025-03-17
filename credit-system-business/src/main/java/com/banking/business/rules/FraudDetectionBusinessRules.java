package com.banking.business.rules;

import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.Customer;
import com.banking.entities.SuspiciousTransaction;
import com.banking.repositories.abstracts.CustomerRepository;
import com.banking.repositories.abstracts.SuspiciousTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dolandırıcılık tespiti için iş kuralları.
 */
@Service
public class FraudDetectionBusinessRules extends BaseBusinessRules<SuspiciousTransaction, Long> {

    private final CustomerRepository customerRepository;
    private final SuspiciousTransactionRepository suspiciousTransactionRepository;
    
    @Value("${fraud.detection.high.amount.threshold:50000}")
    private BigDecimal highAmountThreshold;
    
    @Value("${fraud.detection.medium.amount.threshold:10000}")
    private BigDecimal mediumAmountThreshold;
    
    @Value("${fraud.detection.frequency.threshold:5}")
    private int frequencyThreshold;
    
    @Value("${fraud.detection.time.window.minutes:60}")
    private int timeWindowMinutes;
    
    public FraudDetectionBusinessRules(SuspiciousTransactionRepository suspiciousTransactionRepository,
                                      CustomerRepository customerRepository) {
        super(suspiciousTransactionRepository);
        this.suspiciousTransactionRepository = suspiciousTransactionRepository;
        this.customerRepository = customerRepository;
    }
    
    /**
     * Müşterinin varlığını kontrol eder.
     * 
     * @param customerId Müşteri ID
     */
    public void checkIfCustomerExists(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new BusinessException("Müşteri bulunamadı: " + customerId);
        }
    }
    
    /**
     * Şüpheli işlemin varlığını kontrol eder.
     * 
     * @param transactionId İşlem ID
     */
    public void checkIfSuspiciousTransactionExists(Long transactionId) {
        if (!suspiciousTransactionRepository.existsById(transactionId)) {
            throw new BusinessException("Şüpheli işlem bulunamadı: " + transactionId);
        }
    }
    
    /**
     * İşlem tutarının yüksek olup olmadığını kontrol eder.
     * 
     * @param amount İşlem tutarı
     * @return Tutar yüksekse true, değilse false
     */
    public boolean isHighAmountTransaction(BigDecimal amount) {
        return amount.compareTo(highAmountThreshold) >= 0;
    }
    
    /**
     * İşlem tutarının orta seviyede olup olmadığını kontrol eder.
     * 
     * @param amount İşlem tutarı
     * @return Tutar orta seviyedeyse true, değilse false
     */
    public boolean isMediumAmountTransaction(BigDecimal amount) {
        return amount.compareTo(mediumAmountThreshold) >= 0 && amount.compareTo(highAmountThreshold) < 0;
    }
    
    /**
     * Belirli bir zaman aralığında yapılan işlem sayısının eşik değerini aşıp aşmadığını kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @param transactionType İşlem türü
     * @return Eşik değeri aşılmışsa true, değilse false
     */
    public boolean isFrequencyThresholdExceeded(Long customerId, SuspiciousTransaction.TransactionType transactionType) {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(timeWindowMinutes);
        List<SuspiciousTransaction> transactions = suspiciousTransactionRepository.findByCustomerIdAndTransactionTypeAndTransactionDateAfter(
                customerId, transactionType, startTime);
        
        return transactions.size() >= frequencyThreshold;
    }
    
    /**
     * Müşterinin risk seviyesini kontrol eder.
     * 
     * @param customer Müşteri
     * @return Risk seviyesi yüksekse true, değilse false
     */
    public boolean isCustomerHighRisk(Customer customer) {
        // Burada müşterinin risk seviyesini belirleyen mantık yer alabilir
        // Örneğin, müşterinin geçmiş işlemleri, kredi skoru, vb. faktörler değerlendirilebilir
        
        // Şimdilik basit bir kontrol yapıyoruz
        List<SuspiciousTransaction> transactions = suspiciousTransactionRepository.findByCustomerId(customer.getId());
        long highRiskCount = transactions.stream()
                .filter(t -> t.getRiskLevel() == SuspiciousTransaction.RiskLevel.HIGH)
                .count();
        
        return highRiskCount >= 2;
    }
    
    /**
     * İşlemin şüpheli olup olmadığını kontrol eder.
     * 
     * @param transaction Şüpheli işlem
     * @return İşlem şüpheliyse true, değilse false
     */
    public boolean isTransactionSuspicious(SuspiciousTransaction transaction) {
        return transaction.getRiskScore() >= 50;
    }
} 