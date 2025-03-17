package com.banking.business.services.campaign;

import com.banking.entities.Customer;
import com.banking.entities.enums.CustomerCategory;
import com.banking.entities.enums.CreditType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kampanya yönetimi servisi.
 * Müşterilere özel kredi teklifleri ve kampanyalar sunar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    /**
     * Müşteriye özel kampanyaları getirir
     * 
     * @param customer Müşteri bilgileri
     * @param customerCategory Müşteri kategorisi
     * @param creditScore Kredi skoru
     * @return Müşteriye özel kampanyalar listesi
     */
    public List<Map<String, Object>> getPersonalizedCampaigns(
            Customer customer, 
            CustomerCategory customerCategory, 
            int creditScore) {
        
        log.info("Generating personalized campaigns for customer: {}, category: {}, score: {}", 
                customer.getCustomerNumber(), customerCategory, creditScore);
        
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        // Müşteri kategorisine göre kampanyalar oluştur
        switch (customerCategory) {
            case VIP:
                campaigns.addAll(generateVipCampaigns(customer, creditScore));
                break;
            case STANDARD:
                campaigns.addAll(generateStandardCampaigns(customer, creditScore));
                break;
            case RISKY:
                campaigns.addAll(generateRiskyCampaigns(customer, creditScore));
                break;
        }
        
        // Mevsimsel kampanyalar ekle
        campaigns.addAll(generateSeasonalCampaigns(customer));
        
        log.info("Generated {} personalized campaigns for customer: {}", 
                campaigns.size(), customer.getCustomerNumber());
        
        return campaigns;
    }
    
    /**
     * VIP müşteriler için kampanyalar oluşturur
     */
    private List<Map<String, Object>> generateVipCampaigns(Customer customer, int creditScore) {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        // Düşük faizli mortgage kampanyası
        Map<String, Object> lowRateMortgage = new HashMap<>();
        lowRateMortgage.put("id", "VIP-MORTGAGE-001");
        lowRateMortgage.put("name", "Premium Mortgage Fırsatı");
        lowRateMortgage.put("description", "VIP müşterilerimize özel, piyasanın %25 altında faiz oranıyla mortgage kredisi");
        lowRateMortgage.put("creditType", CreditType.MORTGAGE);
        lowRateMortgage.put("interestRateDiscount", 0.25); // %25 indirim
        lowRateMortgage.put("maxAmount", new BigDecimal("2000000")); // 2 milyon TL'ye kadar
        lowRateMortgage.put("validUntil", LocalDate.now().plusMonths(3));
        campaigns.add(lowRateMortgage);
        
        // Uzun vadeli kişisel kredi
        Map<String, Object> longTermPersonal = new HashMap<>();
        longTermPersonal.put("id", "VIP-PERSONAL-001");
        longTermPersonal.put("name", "VIP Uzun Vadeli Kişisel Kredi");
        longTermPersonal.put("description", "60 aya varan vade seçenekleriyle kişisel kredi");
        longTermPersonal.put("creditType", CreditType.PERSONAL_FINANCE);
        longTermPersonal.put("interestRateDiscount", 0.15); // %15 indirim
        longTermPersonal.put("extendedTerm", 60); // 60 aya kadar vade
        longTermPersonal.put("validUntil", LocalDate.now().plusMonths(6));
        campaigns.add(longTermPersonal);
        
        // Bonus puanlı otomobil kredisi
        Map<String, Object> bonusAutoLoan = new HashMap<>();
        bonusAutoLoan.put("id", "VIP-AUTO-001");
        bonusAutoLoan.put("name", "Premium Otomobil Kredisi + Bonus");
        bonusAutoLoan.put("description", "Otomobil kredinizde 10.000 bonus puan hediye");
        bonusAutoLoan.put("creditType", CreditType.AUTO_LOAN);
        bonusAutoLoan.put("interestRateDiscount", 0.10); // %10 indirim
        bonusAutoLoan.put("bonusPoints", 10000);
        bonusAutoLoan.put("validUntil", LocalDate.now().plusMonths(2));
        campaigns.add(bonusAutoLoan);
        
        return campaigns;
    }
    
    /**
     * Standart müşteriler için kampanyalar oluşturur
     */
    private List<Map<String, Object>> generateStandardCampaigns(Customer customer, int creditScore) {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        // Orta düzey faiz indirimli mortgage
        Map<String, Object> midRateMortgage = new HashMap<>();
        midRateMortgage.put("id", "STD-MORTGAGE-001");
        midRateMortgage.put("name", "Avantajlı Mortgage Fırsatı");
        midRateMortgage.put("description", "Piyasanın %15 altında faiz oranıyla mortgage kredisi");
        midRateMortgage.put("creditType", CreditType.MORTGAGE);
        midRateMortgage.put("interestRateDiscount", 0.15); // %15 indirim
        midRateMortgage.put("maxAmount", new BigDecimal("1000000")); // 1 milyon TL'ye kadar
        midRateMortgage.put("validUntil", LocalDate.now().plusMonths(2));
        campaigns.add(midRateMortgage);
        
        // Masrafsız kişisel kredi
        if (creditScore > 650) {
            Map<String, Object> noFeesPersonal = new HashMap<>();
            noFeesPersonal.put("id", "STD-PERSONAL-001");
            noFeesPersonal.put("name", "Masrafsız Kişisel Kredi");
            noFeesPersonal.put("description", "Dosya masrafı olmadan kişisel kredi fırsatı");
            noFeesPersonal.put("creditType", CreditType.PERSONAL_FINANCE);
            noFeesPersonal.put("noFees", true);
            noFeesPersonal.put("validUntil", LocalDate.now().plusMonths(1));
            campaigns.add(noFeesPersonal);
        }
        
        return campaigns;
    }
    
    /**
     * Riskli müşteriler için kampanyalar oluşturur
     */
    private List<Map<String, Object>> generateRiskyCampaigns(Customer customer, int creditScore) {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        // Düşük miktarlı kişisel kredi
        Map<String, Object> smallPersonal = new HashMap<>();
        smallPersonal.put("id", "RISK-PERSONAL-001");
        smallPersonal.put("name", "Mini Kredi Fırsatı");
        smallPersonal.put("description", "Hızlı onay süreciyle küçük ihtiyaçlarınız için kredi");
        smallPersonal.put("creditType", CreditType.PERSONAL_FINANCE);
        smallPersonal.put("maxAmount", new BigDecimal("10000")); // 10 bin TL'ye kadar
        smallPersonal.put("fastApproval", true);
        smallPersonal.put("validUntil", LocalDate.now().plusMonths(1));
        campaigns.add(smallPersonal);
        
        // Kredi puanı iyileştirme programı
        Map<String, Object> creditImprovement = new HashMap<>();
        creditImprovement.put("id", "RISK-PROGRAM-001");
        creditImprovement.put("name", "Kredi Puanı İyileştirme Programı");
        creditImprovement.put("description", "Kredi puanınızı yükseltmek için özel program ve danışmanlık");
        creditImprovement.put("programType", "CREDIT_IMPROVEMENT");
        creditImprovement.put("duration", 6); // 6 aylık program
        creditImprovement.put("validUntil", LocalDate.now().plusMonths(12));
        campaigns.add(creditImprovement);
        
        return campaigns;
    }
    
    /**
     * Mevsimsel kampanyalar oluşturur
     */
    private List<Map<String, Object>> generateSeasonalCampaigns(Customer customer) {
        List<Map<String, Object>> campaigns = new ArrayList<>();
        
        int currentMonth = LocalDate.now().getMonthValue();
        
        // Yaz kampanyaları (Haziran-Ağustos)
        if (currentMonth >= 6 && currentMonth <= 8) {
            Map<String, Object> summerCampaign = new HashMap<>();
            summerCampaign.put("id", "SEASONAL-SUMMER-001");
            summerCampaign.put("name", "Yaz Tatili Kredisi");
            summerCampaign.put("description", "Tatil planlarınız için özel faiz oranlı kredi");
            summerCampaign.put("creditType", CreditType.PERSONAL_FINANCE);
            summerCampaign.put("interestRateDiscount", 0.10); // %10 indirim
            summerCampaign.put("validUntil", LocalDate.now().withMonth(8).withDayOfMonth(31));
            campaigns.add(summerCampaign);
        }
        
        // Kış kampanyaları (Aralık-Şubat)
        else if (currentMonth == 12 || currentMonth <= 2) {
            Map<String, Object> winterCampaign = new HashMap<>();
            winterCampaign.put("id", "SEASONAL-WINTER-001");
            winterCampaign.put("name", "Kış Fırsatları");
            winterCampaign.put("description", "Yılbaşı ve kış sezonu için özel kredi fırsatları");
            winterCampaign.put("creditType", CreditType.PERSONAL_FINANCE);
            winterCampaign.put("interestRateDiscount", 0.05); // %5 indirim
            winterCampaign.put("bonusPoints", 5000);
            winterCampaign.put("validUntil", LocalDate.now().withMonth(2).withDayOfMonth(28));
            campaigns.add(winterCampaign);
        }
        
        // Eğitim sezonu kampanyaları (Ağustos-Eylül)
        else if (currentMonth >= 8 && currentMonth <= 9) {
            Map<String, Object> educationCampaign = new HashMap<>();
            educationCampaign.put("id", "SEASONAL-EDU-001");
            educationCampaign.put("name", "Eğitim Kredisi Fırsatı");
            educationCampaign.put("description", "Okul sezonu için uygun faizli eğitim kredisi");
            educationCampaign.put("creditType", CreditType.EDUCATION_LOAN);
            educationCampaign.put("interestRateDiscount", 0.20); // %20 indirim
            educationCampaign.put("validUntil", LocalDate.now().withMonth(9).withDayOfMonth(30));
            campaigns.add(educationCampaign);
        }
        
        return campaigns;
    }
    
    /**
     * Belirli bir kampanya için faiz oranı hesaplar
     * 
     * @param campaignId Kampanya ID
     * @param baseRate Temel faiz oranı
     * @return İndirimli faiz oranı
     */
    public BigDecimal calculateCampaignRate(String campaignId, BigDecimal baseRate) {
        // Gerçek uygulamada bu bilgiler veritabanından alınır
        
        // Kampanya ID'sine göre indirim oranını belirle
        double discountRate = 0.0;
        
        if (campaignId.startsWith("VIP-")) {
            discountRate = 0.25; // VIP kampanyaları için %25 indirim
        } else if (campaignId.startsWith("STD-")) {
            discountRate = 0.15; // Standart kampanyalar için %15 indirim
        } else if (campaignId.startsWith("SEASONAL-")) {
            discountRate = 0.10; // Mevsimsel kampanyalar için %10 indirim
        } else {
            discountRate = 0.05; // Diğer kampanyalar için %5 indirim
        }
        
        // İndirimli faiz oranını hesapla
        BigDecimal discountAmount = baseRate.multiply(new BigDecimal(discountRate));
        return baseRate.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Kampanyanın geçerli olup olmadığını kontrol eder
     * 
     * @param campaignId Kampanya ID
     * @return Kampanya geçerli mi?
     */
    public boolean isCampaignValid(String campaignId) {
        // Gerçek uygulamada bu bilgiler veritabanından alınır
        
        // Örnek olarak bazı kampanyaların süresi dolmuş olsun
        List<String> expiredCampaigns = List.of(
                "VIP-AUTO-001",
                "STD-PERSONAL-002",
                "RISK-PROGRAM-002"
        );
        
        return !expiredCampaigns.contains(campaignId);
    }
} 