package com.banking.core.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT token oluşturma, doğrulama ve işleme servisi.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * JWT token'dan kullanıcı adını çıkarır.
     *
     * @param token JWT token
     * @return Kullanıcı adı
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWT token'dan belirli bir claim'i çıkarır.
     *
     * @param token JWT token
     * @param claimsResolver Claim çözümleyici fonksiyon
     * @return Çıkarılan claim değeri
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Kullanıcı detaylarına göre JWT token oluşturur.
     *
     * @param userDetails Kullanıcı detayları
     * @return Oluşturulan JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Ek claim'ler ve kullanıcı detaylarına göre JWT token oluşturur.
     *
     * @param extraClaims Ek claim'ler
     * @param userDetails Kullanıcı detayları
     * @return Oluşturulan JWT token
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Yenileme token'ı oluşturur.
     *
     * @param userDetails Kullanıcı detayları
     * @return Oluşturulan yenileme token'ı
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Token oluşturma işlemini gerçekleştirir.
     *
     * @param extraClaims Ek claim'ler
     * @param userDetails Kullanıcı detayları
     * @param expiration Geçerlilik süresi
     * @return Oluşturulan token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token'ın geçerli olup olmadığını kontrol eder.
     *
     * @param token JWT token
     * @param userDetails Kullanıcı detayları
     * @return Token geçerli ise true, değilse false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Token'ın süresinin dolup dolmadığını kontrol eder.
     *
     * @param token JWT token
     * @return Token süresi dolmuşsa true, dolmamışsa false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Token'ın son geçerlilik tarihini çıkarır.
     *
     * @param token JWT token
     * @return Son geçerlilik tarihi
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Token'dan tüm claim'leri çıkarır.
     *
     * @param token JWT token
     * @return Tüm claim'ler
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * İmzalama anahtarını oluşturur.
     *
     * @return İmzalama anahtarı
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 