package com.banking.core.security.twofa;

import com.banking.core.notifications.EmailService;
import com.banking.core.notifications.SmsService;
import com.banking.entities.OtpVerification;
import com.banking.entities.User;
import com.banking.entities.enums.VerificationChannel;
import com.banking.repositories.abstracts.OtpVerificationRepository;
import com.banking.repositories.abstracts.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * İki faktörlü kimlik doğrulama servisi.
 * SMS, Email ve Google Authenticator ile OTP doğrulama işlemlerini yönetir.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthService {

    private final OtpVerificationRepository otpVerificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final GoogleAuthenticatorService googleAuthenticatorService;
    
    @Value("${twofa.otp.length:6}")
    private int otpLength;
    
    @Value("${twofa.otp.expiration.minutes:5}")
    private int otpExpirationMinutes;
    
    @Value("${twofa.enabled:true}")
    private boolean twoFactorAuthEnabled;
    
    /**
     * Kullanıcı için SMS ile OTP gönderir.
     * 
     * @param userId Kullanıcı ID
     * @return Başarılı ise true, değilse false
     */
    @Transactional
    public boolean sendSmsOtp(Long userId) {
        if (!twoFactorAuthEnabled) {
            log.info("Two-factor authentication is disabled. Skipping SMS OTP for user: {}", userId);
            return true;
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        String otpCode = generateOtpCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
        
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUserId(userId);
        otpVerification.setOtpCode(otpCode);
        otpVerification.setVerificationChannel(VerificationChannel.SMS);
        otpVerification.setExpirationTime(expirationTime);
        otpVerification.setVerified(false);
        otpVerification.setAttemptCount(0);
        
        otpVerificationRepository.save(otpVerification);
        
        String message = "Bankacılık uygulaması için doğrulama kodunuz: " + otpCode + ". Bu kod " + otpExpirationMinutes + " dakika geçerlidir.";
        return smsService.sendSms(user.getPhoneNumber(), message);
    }
    
    /**
     * Kullanıcı için Email ile OTP gönderir.
     * 
     * @param userId Kullanıcı ID
     * @return Başarılı ise true, değilse false
     */
    @Transactional
    public boolean sendEmailOtp(Long userId) {
        if (!twoFactorAuthEnabled) {
            log.info("Two-factor authentication is disabled. Skipping Email OTP for user: {}", userId);
            return true;
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        String otpCode = generateOtpCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
        
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUserId(userId);
        otpVerification.setOtpCode(otpCode);
        otpVerification.setVerificationChannel(VerificationChannel.EMAIL);
        otpVerification.setExpirationTime(expirationTime);
        otpVerification.setVerified(false);
        otpVerification.setAttemptCount(0);
        
        otpVerificationRepository.save(otpVerification);
        
        String subject = "Bankacılık Uygulaması - Doğrulama Kodu";
        String content = "Sayın " + user.getFirstName() + " " + user.getLastName() + ",\n\n" +
                "Bankacılık uygulaması için doğrulama kodunuz: " + otpCode + "\n\n" +
                "Bu kod " + otpExpirationMinutes + " dakika geçerlidir.\n\n" +
                "Eğer bu işlemi siz yapmadıysanız, lütfen hemen müşteri hizmetlerimizle iletişime geçin.\n\n" +
                "Saygılarımızla,\nBankacılık Uygulaması";
        
        return emailService.sendEmail(user.getEmail(), subject, content, Map.of("otpCode", otpCode));
    }
    
    /**
     * Google Authenticator için QR kod URL'si oluşturur.
     * 
     * @param userId Kullanıcı ID
     * @return QR kod URL'si
     */
    @Transactional
    public String setupGoogleAuthenticator(Long userId) {
        if (!twoFactorAuthEnabled) {
            log.info("Two-factor authentication is disabled. Skipping Google Authenticator setup for user: {}", userId);
            return "";
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        String secretKey = googleAuthenticatorService.generateSecretKey();
        String qrCodeUrl = googleAuthenticatorService.generateQrCodeUrl(user.getEmail(), secretKey);
        
        // Kullanıcı için Google Authenticator ayarlarını kaydet
        // Bu örnekte sadece OTP kaydı oluşturuyoruz, gerçek uygulamada kullanıcı tablosuna secretKey eklenmelidir
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setUserId(userId);
        otpVerification.setOtpCode(secretKey);
        otpVerification.setVerificationChannel(VerificationChannel.GOOGLE_AUTHENTICATOR);
        otpVerification.setExpirationTime(LocalDateTime.now().plusYears(1)); // Google Authenticator için uzun süreli
        otpVerification.setVerified(false);
        otpVerification.setAttemptCount(0);
        
        otpVerificationRepository.save(otpVerification);
        
        return qrCodeUrl;
    }
    
    /**
     * OTP kodunu doğrular.
     * 
     * @param userId Kullanıcı ID
     * @param otpCode OTP kodu
     * @param channel Doğrulama kanalı
     * @return Doğrulama başarılı ise true, değilse false
     */
    @Transactional
    public boolean verifyOtp(Long userId, String otpCode, VerificationChannel channel) {
        if (!twoFactorAuthEnabled) {
            log.info("Two-factor authentication is disabled. Skipping OTP verification for user: {}", userId);
            return true;
        }
        
        if (channel == VerificationChannel.GOOGLE_AUTHENTICATOR) {
            // Google Authenticator için özel doğrulama
            Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository.findLatestByUserIdAndChannel(
                    userId, VerificationChannel.GOOGLE_AUTHENTICATOR);
            
            if (otpVerificationOpt.isPresent()) {
                OtpVerification otpVerification = otpVerificationOpt.get();
                String secretKey = otpVerification.getOtpCode();
                
                boolean isValid = googleAuthenticatorService.verifyCode(secretKey, otpCode);
                if (isValid) {
                    // Google Authenticator kodları tek kullanımlık değil, bu yüzden verified olarak işaretlemiyoruz
                    return true;
                } else {
                    otpVerification.incrementAttemptCount();
                    otpVerificationRepository.save(otpVerification);
                    return false;
                }
            }
            
            return false;
        } else {
            // SMS ve Email için standart OTP doğrulama
            Optional<OtpVerification> otpVerificationOpt = otpVerificationRepository.findValidOtp(
                    userId, otpCode, LocalDateTime.now());
            
            if (otpVerificationOpt.isPresent()) {
                OtpVerification otpVerification = otpVerificationOpt.get();
                
                if (otpVerification.isValid() && otpVerification.getVerificationChannel() == channel) {
                    otpVerification.markAsVerified();
                    otpVerificationRepository.save(otpVerification);
                    return true;
                } else {
                    otpVerification.incrementAttemptCount();
                    otpVerificationRepository.save(otpVerification);
                    return false;
                }
            }
            
            return false;
        }
    }
    
    /**
     * Rastgele OTP kodu oluşturur.
     * 
     * @return OTP kodu
     */
    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(otpLength);
        
        for (int i = 0; i < otpLength; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
    
    /**
     * Süresi dolmuş OTP kayıtlarını temizler.
     * Her gün gece yarısı çalışır.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = otpVerificationRepository.cleanupExpiredOtps(now);
        log.info("Cleaned up {} expired OTP records", deletedCount);
    }
} 