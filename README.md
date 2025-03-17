# 🏦 Bankacılık Kredi Sistemi

Modern bir bankacılık kredi sistemi uygulaması. Müşteri yönetimi, kredi başvuruları, risk analizi, ödemeler ve daha fazlasını içeren kapsamlı bir çözüm.

## 🌟 Özellikler

- **👥 Müşteri Yönetimi**
  - Bireysel ve Kurumsal Müşteri Kaydı
  - İki Faktörlü Kimlik Doğrulama (2FA)
  - E-İmza Entegrasyonu (Türkiye)
  - KPS (Kimlik Paylaşım Sistemi) Entegrasyonu

- **💳 Kredi İşlemleri**
  - Kredi Başvuru Yönetimi
  - Otomatik Kredi Skoru Hesaplama
  - Risk Analizi ve Değerlendirme
  - Kredi Limit Yönetimi

- **🔒 Güvenlik**
  - JWT Tabanlı Kimlik Doğrulama
  - Rol Tabanlı Yetkilendirme
  - API Rate Limiting
  - Hassas Veri Şifreleme

- **💰 Ödeme İşlemleri**
  - Kredi Ödemeleri
  - Otomatik Ödeme Planı
  - Gecikmiş Ödeme Takibi
  - İade İşlemleri

## 🛠️ Teknolojiler

- Java 21
- Spring Boot 3.4.3
- Spring Security
- PostgreSQL
- Redis
- Maven
- MapStruct
- Lombok
- Swagger/OpenAPI
- JWT
- Bucket4j

## 📋 Gereksinimler

- Java 21 veya üzeri
- Maven 3.8 veya üzeri
- PostgreSQL 15 veya üzeri
- Redis 7 veya üzeri

## 🚀 Kurulum

1. **Repository'yi Klonlayın**
   ```bash
   git clone https://github.com/kullaniciadi/credit-system.git
   cd credit-system
   ```

2. **PostgreSQL Veritabanını Oluşturun**
   ```sql
   CREATE DATABASE credit_system;
   ```

3. **application.properties Dosyasını Düzenleyin**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/credit_system
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Projeyi Derleyin**
   ```bash
   mvn clean install
   ```

5. **Uygulamayı Çalıştırın**
   ```bash
   cd credit-system-webapi
   mvn spring-boot:run
   ```

## 📚 API Dokümantasyonu

Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🏗️ Proje Yapısı

```
credit-system/
├── credit-system-core/        # Çekirdek bileşenler ve utilities
├── credit-system-entities/    # JPA entities
├── credit-system-repositories/# Veritabanı repository'leri
├── credit-system-business/    # İş mantığı ve servisler
└── credit-system-webapi/     # REST API ve controllers
```

## 🔑 Güvenlik Yapılandırması

1. **JWT Yapılandırması**
   ```properties
   jwt.secret=your_jwt_secret_key
   jwt.expiration=86400000
   ```

2. **Rate Limiting Yapılandırması**
   ```properties
   rate-limit.capacity=100
   rate-limit.time-window-minutes=1
   ```

## 🧪 Test

```bash
mvn test
```

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📞 İletişim

- Proje Sorumlusu - [@github_username](https://github.com/github_username)
- Proje Linki: [https://github.com/username/credit-system](https://github.com/username/credit-system)

## 🙏 Teşekkürler

- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/)
- [Redis](https://redis.io/)
- Diğer tüm açık kaynak kütüphaneler 
