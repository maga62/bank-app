package com.banking.business.services.scoring;

import com.banking.entities.Customer;
import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CustomerCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * AI destekli kredi skorlama servisi.
 * Müşteri verilerini, kredi geçmişini, banka hareketlerini ve gelir-gider oranını analiz ederek
 * kredi skoru hesaplar.
 */
@Service
@Slf4j
public class CreditScoringService {

    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 900;
    private static final int THRESHOLD_SCORE_VIP = 750;
    private static final int THRESHOLD_SCORE_STANDARD = 600;
    
    /**
     * Müşteri için kredi skoru hesaplar
     * 
     * @param customer Müşteri bilgileri
     * @param application Kredi başvuru bilgileri
     * @param financialData Finansal veriler (gelir, gider, mevcut krediler, ödeme geçmişi vb.)
     * @return Hesaplanan kredi skoru (300-900 arası)
     */
    public int calculateCreditScore(Customer customer, CreditApplication application, Map<String, Object> financialData) {
        log.info("Calculating credit score for customer: {}", customer.getCustomerNumber());
        
        // Temel skor hesaplama
        int baseScore = calculateBaseScore(customer);
        
        // Gelir-gider oranına göre skor ayarlaması
        int incomeExpenseScore = calculateIncomeExpenseScore(application, financialData);
        
        // Kredi geçmişine göre skor ayarlaması
        int creditHistoryScore = calculateCreditHistoryScore(financialData);
        
        // Banka hareketlerine göre skor ayarlaması
        int bankActivityScore = calculateBankActivityScore(financialData);
        
        // Toplam skor hesaplama
        int totalScore = baseScore + incomeExpenseScore + creditHistoryScore + bankActivityScore;
        
        // Skor sınırlarını kontrol et
        totalScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, totalScore));
        
        log.info("Credit score calculated for customer {}: {}", customer.getCustomerNumber(), totalScore);
        return totalScore;
    }
    
    /**
     * Kredi skoruna göre müşteri kategorisini belirler
     * 
     * @param creditScore Hesaplanan kredi skoru
     * @return Müşteri kategorisi (VIP, STANDARD, RISKY)
     */
    public CustomerCategory determineCustomerCategory(int creditScore) {
        if (creditScore >= THRESHOLD_SCORE_VIP) {
            return CustomerCategory.VIP;
        } else if (creditScore >= THRESHOLD_SCORE_STANDARD) {
            return CustomerCategory.STANDARD;
        } else {
            return CustomerCategory.RISKY;
        }
    }
    
    /**
     * Temel müşteri bilgilerine göre baz skor hesaplar
     */
    private int calculateBaseScore(Customer customer) {
        // Gerçek uygulamada burada müşteri yaşı, mesleği, eğitim durumu gibi faktörler değerlendirilir
        // Şimdilik basit bir hesaplama yapıyoruz
        return 500; // Baz skor
    }
    
    /**
     * Gelir-gider oranına göre skor hesaplar
     */
    private int calculateIncomeExpenseScore(CreditApplication application, Map<String, Object> financialData) {
        BigDecimal monthlyIncome = application.getMonthlyIncome();
        BigDecimal monthlyExpenses = (BigDecimal) financialData.getOrDefault("monthlyExpenses", BigDecimal.ZERO);
        
        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return -100; // Gelir bilgisi yoksa veya sıfırsa negatif etki
        }
        
        // Gelir-gider oranı hesaplama
        BigDecimal ratio = monthlyExpenses.divide(monthlyIncome, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        
        // Oran 0.5'ten küçükse çok iyi (düşük gider/yüksek gelir)
        if (ratio.compareTo(new BigDecimal("0.5")) < 0) {
            return 150;
        }
        // Oran 0.5-0.7 arasında ise iyi
        else if (ratio.compareTo(new BigDecimal("0.7")) < 0) {
            return 100;
        }
        // Oran 0.7-0.9 arasında ise orta
        else if (ratio.compareTo(new BigDecimal("0.9")) < 0) {
            return 50;
        }
        // Oran 0.9'dan büyükse kötü (yüksek gider/düşük gelir)
        else {
            return -50;
        }
    }
    
    /**
     * Kredi geçmişine göre skor hesaplar
     */
    private int calculateCreditHistoryScore(Map<String, Object> financialData) {
        Integer latePayments = (Integer) financialData.getOrDefault("latePayments", 0);
        Integer onTimePayments = (Integer) financialData.getOrDefault("onTimePayments", 0);
        
        // Geç ödemeler için negatif puan
        int latePaymentScore = latePayments * -20;
        
        // Zamanında ödemeler için pozitif puan
        int onTimePaymentScore = onTimePayments * 10;
        
        return latePaymentScore + onTimePaymentScore;
    }
    
    /**
     * Banka hareketlerine göre skor hesaplar
     */
    private int calculateBankActivityScore(Map<String, Object> financialData) {
        Integer accountAge = (Integer) financialData.getOrDefault("accountAgeMonths", 0);
        BigDecimal averageBalance = (BigDecimal) financialData.getOrDefault("averageBalance", BigDecimal.ZERO);
        
        // Hesap yaşı puanı (her yıl için 5 puan, max 50)
        int accountAgeScore = Math.min(50, (accountAge / 12) * 5);
        
        // Ortalama bakiye puanı
        int balanceScore = 0;
        if (averageBalance.compareTo(new BigDecimal("10000")) > 0) {
            balanceScore = 100;
        } else if (averageBalance.compareTo(new BigDecimal("5000")) > 0) {
            balanceScore = 50;
        } else if (averageBalance.compareTo(new BigDecimal("1000")) > 0) {
            balanceScore = 25;
        }
        
        return accountAgeScore + balanceScore;
    }
    
    /**
     * Test amaçlı finansal veri oluşturur
     * Gerçek uygulamada bu veriler veritabanından veya harici servislerden gelir
     */
    public Map<String, Object> generateTestFinancialData() {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();
        
        // Aylık giderler (gelirin %50-90'ı arasında)
        data.put("monthlyExpenses", new BigDecimal(random.nextInt(5000) + 2000));
        
        // Geç ve zamanında ödemeler
        data.put("latePayments", random.nextInt(3));
        data.put("onTimePayments", random.nextInt(20) + 5);
        
        // Hesap yaşı (ay cinsinden)
        data.put("accountAgeMonths", random.nextInt(60) + 6);
        
        // Ortalama bakiye
        data.put("averageBalance", new BigDecimal(random.nextInt(20000) + 1000));
        
        return data;
    }
} 