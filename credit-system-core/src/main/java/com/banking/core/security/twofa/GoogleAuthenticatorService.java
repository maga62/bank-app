package com.banking.core.security.twofa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * Google Authenticator ile TOTP (Time-based One-Time Password) işlemlerini yönetir.
 */
@Service
@Slf4j
public class GoogleAuthenticatorService {

    @Value("${google.authenticator.issuer:BankingApp}")
    private String issuer;
    
    @Value("${google.authenticator.time.step:30}")
    private int timeStepSeconds;
    
    @Value("${google.authenticator.window.size:1}")
    private int windowSize;
    
    /**
     * Yeni bir secret key oluşturur.
     * 
     * @return Base32 kodlanmış secret key
     */
    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160 bit
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
    
    /**
     * Google Authenticator uygulaması için QR kod URL'si oluşturur.
     * 
     * @param accountName Hesap adı (genellikle email)
     * @param secretKey Secret key
     * @return QR kod URL'si
     */
    public String generateQrCodeUrl(String accountName, String secretKey) {
        String encodedIssuer = encodeUrl(issuer);
        String encodedAccount = encodeUrl(accountName);
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", 
                encodedIssuer, encodedAccount, secretKey, encodedIssuer);
    }
    
    /**
     * Kullanıcının girdiği kodu doğrular.
     * 
     * @param secretKey Secret key
     * @param code Kullanıcının girdiği kod
     * @return Kod doğruysa true, değilse false
     */
    public boolean verifyCode(String secretKey, String code) {
        try {
            Base32 base32 = new Base32();
            byte[] bytes = base32.decode(secretKey);
            
            long currentTimeMillis = System.currentTimeMillis();
            long currentTimeStep = currentTimeMillis / (timeStepSeconds * 1000);
            
            // Zaman penceresi içindeki tüm adımları kontrol et
            for (int i = -windowSize; i <= windowSize; i++) {
                long timeStep = currentTimeStep + i;
                String expectedCode = generateCode(bytes, timeStep);
                
                if (expectedCode.equals(code)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error verifying Google Authenticator code", e);
            return false;
        }
    }
    
    /**
     * Belirli bir zaman adımı için TOTP kodu oluşturur.
     * 
     * @param key Secret key (byte array)
     * @param timeStep Zaman adımı
     * @return 6 haneli TOTP kodu
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String generateCode(byte[] key, long timeStep) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] timeBytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            timeBytes[i] = (byte) (timeStep & 0xff);
            timeStep >>= 8;
        }
        
        // HMAC-SHA1 algoritması ile hash oluştur
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(timeBytes);
        
        // Son baytın son 4 biti ile offset belirle
        int offset = hash[hash.length - 1] & 0xf;
        
        // 4 baytlık bir değer oluştur
        int binary = ((hash[offset] & 0x7f) << 24) |
                     ((hash[offset + 1] & 0xff) << 16) |
                     ((hash[offset + 2] & 0xff) << 8) |
                     (hash[offset + 3] & 0xff);
        
        // 6 haneli kod oluştur
        int otp = binary % 1000000;
        return String.format("%06d", otp);
    }
    
    /**
     * URL için string kodlar.
     * 
     * @param input Kodlanacak string
     * @return Kodlanmış string
     */
    private String encodeUrl(String input) {
        return input.replace(" ", "%20");
    }
} 