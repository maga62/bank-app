package com.banking.entities;

import com.banking.entities.enums.VerificationChannel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OTP doğrulama bilgilerini saklayan entity sınıfı.
 */
@Entity
@Table(name = "otp_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "otp_code", nullable = false)
    private String otpCode;
    
    @Column(name = "verification_channel")
    @Enumerated(EnumType.STRING)
    private VerificationChannel verificationChannel;
    
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
    
    @Column(name = "verified")
    private boolean verified;
    
    @Column(name = "attempt_count")
    private int attemptCount;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
    
    /**
     * OTP doğrulama kanalı.
     */
    public enum OTPStatus {
        PENDING,
        VERIFIED,
        EXPIRED,
        FAILED
    }
    
    /**
     * OTP kodunun geçerli olup olmadığını kontrol eder.
     * 
     * @return OTP kodu geçerliyse true, değilse false
     */
    public boolean isValid() {
        return !verified && expirationTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Deneme sayısını artırır.
     */
    public void incrementAttemptCount() {
        this.attemptCount++;
    }
    
    /**
     * OTP doğrulamasını tamamlar.
     */
    public void markAsVerified() {
        this.verified = true;
    }
} 