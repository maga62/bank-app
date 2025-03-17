package com.banking.webapi.services.auth;

import com.banking.entities.User;
import com.banking.repositories.abstracts.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security için özel UserDetailsService implementasyonu.
 * Veritabanından kullanıcı bilgilerini yükler ve Spring Security'nin anlayacağı formata dönüştürür.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Kullanıcı adına göre kullanıcı detaylarını yükler.
     *
     * @param username Kullanıcı adı
     * @return UserDetails nesnesi
     * @throws UsernameNotFoundException Kullanıcı bulunamazsa fırlatılır
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Kullanıcı yükleniyor: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Kullanıcı bulunamadı: {}", username);
                    return new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
                });
        
        if (!user.isActive()) {
            log.error("Kullanıcı aktif değil: {}", username);
            throw new UsernameNotFoundException("Kullanıcı aktif değil: " + username);
        }
        
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        
        log.debug("Kullanıcı yüklendi: {} - Roller: {}", username, authorities);
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
} 