package com.banking.webapi.services.auth;

import com.banking.core.security.jwt.JwtService;
import com.banking.entities.User;
import com.banking.entities.enums.Role;
import com.banking.repositories.abstracts.UserRepository;
import com.banking.webapi.dtos.auth.AuthenticationRequest;
import com.banking.webapi.dtos.auth.AuthenticationResponse;
import com.banking.webapi.dtos.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * Kimlik doğrulama işlemlerini yöneten servis sınıfı.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Yeni kullanıcı kaydı yapar.
     *
     * @param request Kayıt isteği
     * @return JWT token içeren yanıt
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Yeni kullanıcı kaydı: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        if (request.getRoles() != null && request.getRoles().length > 0) {
            for (String roleStr : request.getRoles()) {
                user.getRoles().add(Role.valueOf(roleStr));
            }
        } else {
            user.getRoles().add(Role.ROLE_USER);
        }
        
        User savedUser = userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .roles(savedUser.getRoles().stream()
                        .map(Enum::name)
                        .toArray(String[]::new))
                .build();
    }

    /**
     * Kullanıcı girişi yapar.
     *
     * @param request Giriş isteği
     * @return JWT token içeren yanıt
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Kullanıcı girişi: {}", request.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        
        String[] roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toArray(String[]::new);
        
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    /**
     * Token yenileme işlemi yapar.
     *
     * @param request Token yenileme isteği
     * @return Yeni JWT token içeren yanıt
     */
    public AuthenticationResponse refreshToken(AuthenticationRequest request) {
        log.info("Token yenileme: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        
        String[] roles = user.getRoles().stream()
                .map(Enum::name)
                .toArray(String[]::new);
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }
} 