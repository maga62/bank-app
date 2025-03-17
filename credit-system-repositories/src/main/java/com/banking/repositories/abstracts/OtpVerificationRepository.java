package com.banking.repositories.abstracts;

import com.banking.entities.OtpVerification;
import com.banking.entities.enums.VerificationChannel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * OTP doğrulama işlemleri için repository sınıfı.
 */
@Repository
public interface OtpVerificationRepository extends GenericRepository<OtpVerification, Long> {
    
    /**
     * Kullanıcı ID ve OTP koduna göre geçerli bir OTP doğrulama kaydı arar.
     * 
     * @param userId Kullanıcı ID
     * @param otpCode OTP kodu
     * @param now Şu anki zaman
     * @return Geçerli OTP doğrulama kaydı
     */
    @Query("SELECT o FROM OtpVerification o WHERE o.userId = :userId AND o.otpCode = :otpCode AND o.expirationTime > :now AND o.verified = false")
    Optional<OtpVerification> findValidOtp(@Param("userId") Long userId, @Param("otpCode") String otpCode, @Param("now") LocalDateTime now);
    
    /**
     * Kullanıcı ID'sine göre en son oluşturulan OTP doğrulama kaydını arar.
     * 
     * @param userId Kullanıcı ID
     * @return En son OTP doğrulama kaydı
     */
    @Query("SELECT o FROM OtpVerification o WHERE o.userId = :userId ORDER BY o.createdDate DESC")
    Optional<OtpVerification> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * Kullanıcı ID ve doğrulama kanalına göre en son oluşturulan OTP doğrulama kaydını arar.
     * 
     * @param userId Kullanıcı ID
     * @param channel Doğrulama kanalı
     * @return En son OTP doğrulama kaydı
     */
    @Query("SELECT o FROM OtpVerification o WHERE o.userId = :userId AND o.verificationChannel = :channel ORDER BY o.createdDate DESC")
    Optional<OtpVerification> findLatestByUserIdAndChannel(@Param("userId") Long userId, @Param("channel") VerificationChannel channel);
    
    /**
     * Süresi dolmuş ve doğrulanmamış OTP kayıtlarını temizler.
     * 
     * @param expirationTime Süre dolum zamanı
     * @return Temizlenen kayıt sayısı
     */
    @Query("UPDATE OtpVerification o SET o.deletedDate = CURRENT_TIMESTAMP WHERE o.expirationTime < :expirationTime AND o.verified = false")
    int cleanupExpiredOtps(@Param("expirationTime") LocalDateTime expirationTime);
} 