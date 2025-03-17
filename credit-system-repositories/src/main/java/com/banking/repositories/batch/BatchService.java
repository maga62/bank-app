package com.banking.repositories.batch;

import com.banking.entities.CreditHistory;
import com.banking.entities.Customer;
import com.banking.entities.PaymentHistory;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.PaymentStatus;
import com.banking.repositories.abstracts.CreditHistoryRepository;
import com.banking.repositories.abstracts.CustomerRepository;
import com.banking.repositories.abstracts.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for batch operations.
 * Provides methods for efficient bulk insert and update operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRepository<Customer> customerRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    
    private static final int BATCH_SIZE = 1000;
    
    /**
     * Batch update credit scores for customers.
     * Updates credit scores based on payment history.
     *
     * @return the number of updated records
     */
    @Transactional
    public int batchUpdateCreditScores() {
        log.info("Starting batch update of credit scores");
        
        int totalUpdated = 0;
        
        // Get all customers in batches
        int page = 0;
        List<?> customersRaw;
        
        do {
            customersRaw = customerRepository.findAll()
                    .stream()
                    .skip((long) page * BATCH_SIZE)
                    .limit(BATCH_SIZE)
                    .toList();
            
            @SuppressWarnings("unchecked")
            List<Customer> customers = (List<Customer>) customersRaw;
            
            for (Customer customer : customers) {
                // Calculate credit score based on payment history
                Integer scoreIncrease = calculateCreditScoreIncrease(customer.getId());
                
                // Update credit score in database
                int updated = creditHistoryRepository.batchUpdateCreditScores(customer.getId(), scoreIncrease);
                totalUpdated += updated;
            }
            
            page++;
        } while (!customersRaw.isEmpty());
        
        log.info("Completed batch update of credit scores. Total updated: {}", totalUpdated);
        
        return totalUpdated;
    }
    
    /**
     * Calculate credit score increase based on payment history.
     *
     * @param customerId the customer ID
     * @return the credit score increase
     */
    private Integer calculateCreditScoreIncrease(Long customerId) {
        // Get on-time payment percentage
        Double onTimePaymentPercentage = creditHistoryRepository.calculateOnTimePaymentPercentageByCustomer(customerId);
        
        if (onTimePaymentPercentage == null) {
            return 0;
        }
        
        // Calculate score increase based on on-time payment percentage
        if (onTimePaymentPercentage >= 95) {
            return 30; // Excellent payment history
        } else if (onTimePaymentPercentage >= 90) {
            return 20; // Very good payment history
        } else if (onTimePaymentPercentage >= 80) {
            return 10; // Good payment history
        } else if (onTimePaymentPercentage >= 70) {
            return 5; // Fair payment history
        } else {
            return 0; // Poor payment history
        }
    }
    
    /**
     * Batch update payment statuses for overdue payments.
     *
     * @return the number of updated records
     */
    @Transactional
    public int batchUpdateOverduePayments() {
        log.info("Starting batch update of overdue payments");
        
        LocalDate currentDate = LocalDate.now();
        
        int updated = paymentHistoryRepository.batchUpdatePaymentStatusForOverduePayments(
                currentDate, PaymentStatus.OVERDUE);
        
        log.info("Completed batch update of overdue payments. Total updated: {}", updated);
        
        return updated;
    }
    
    /**
     * Batch update credit history statuses for delinquent accounts.
     *
     * @return the number of updated records
     */
    @Transactional
    public int batchUpdateDelinquentAccounts() {
        log.info("Starting batch update of delinquent accounts");
        
        // Get all credit histories with overdue payments
        List<CreditHistory> creditHistories = creditHistoryRepository.findAll();
        
        int totalUpdated = 0;
        
        for (CreditHistory creditHistory : creditHistories) {
            // Check if credit history has late payments
            if (creditHistory.getLatePayments() != null && creditHistory.getLatePayments() > 3) {
                // Update status to DELINQUENT
                creditHistory.setStatus(CreditHistoryStatus.DELINQUENT);
                creditHistoryRepository.save(creditHistory);
                totalUpdated++;
            } else if (creditHistory.getLatePayments() != null && creditHistory.getLatePayments() > 6) {
                // Update status to DEFAULT
                creditHistory.setStatus(CreditHistoryStatus.DEFAULT);
                creditHistoryRepository.save(creditHistory);
                totalUpdated++;
            }
        }
        
        log.info("Completed batch update of delinquent accounts. Total updated: {}", totalUpdated);
        
        return totalUpdated;
    }
    
    /**
     * Batch insert payment histories.
     *
     * @param paymentHistories the payment histories
     * @return the number of inserted records
     */
    @Transactional
    public int batchInsertPaymentHistories(List<PaymentHistory> paymentHistories) {
        log.info("Starting batch insert of {} payment histories", paymentHistories.size());
        
        String sql = """
                INSERT INTO payment_histories (
                    credit_application_id, customer_id, repayment_plan_id, due_date, payment_date,
                    amount_due, amount_paid, payment_status, days_late, late_fee,
                    payment_method, transaction_reference, notes, created_date, updated_date
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                PaymentHistory paymentHistory = paymentHistories.get(i);
                ps.setLong(1, paymentHistory.getCreditApplication().getId());
                ps.setLong(2, paymentHistory.getCustomer().getId());
                ps.setObject(3, paymentHistory.getRepaymentPlan() != null ? paymentHistory.getRepaymentPlan().getId() : null);
                ps.setDate(4, java.sql.Date.valueOf(paymentHistory.getDueDate()));
                ps.setDate(5, paymentHistory.getPaymentDate() != null ? java.sql.Date.valueOf(paymentHistory.getPaymentDate()) : null);
                ps.setBigDecimal(6, paymentHistory.getAmountDue());
                ps.setBigDecimal(7, paymentHistory.getAmountPaid());
                ps.setString(8, paymentHistory.getPaymentStatus().name());
                ps.setObject(9, paymentHistory.getDaysLate());
                ps.setBigDecimal(10, paymentHistory.getLateFee());
                ps.setString(11, paymentHistory.getPaymentMethod());
                ps.setString(12, paymentHistory.getTransactionReference());
                ps.setString(13, paymentHistory.getNotes());
                ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(15, null); // deletedDate
            }
            
            @Override
            public int getBatchSize() {
                return paymentHistories.size();
            }
        });
        
        int totalInserted = 0;
        for (int count : batchUpdate) {
            totalInserted += count;
        }
        
        log.info("Completed batch insert of payment histories. Total inserted: {}", totalInserted);
        
        return totalInserted;
    }
    
    /**
     * Process credit history updates asynchronously.
     * 
     * @param creditHistories the credit histories to process
     * @return the completable future
     */
    @Async
    public CompletableFuture<Integer> processAsyncCreditHistoryUpdates(List<CreditHistory> creditHistories) {
        log.info("Starting async processing of {} credit histories", creditHistories.size());
        
        int totalProcessed = 0;
        
        for (int i = 0; i < creditHistories.size(); i++) {
            // Process credit history
            // Burada gerçek işlemler yapılacak
            
            totalProcessed++;
        }
        
        log.info("Completed async processing of credit histories. Total processed: {}", totalProcessed);
        return CompletableFuture.completedFuture(totalProcessed);
    }
} 