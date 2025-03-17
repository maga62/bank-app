package com.banking.business.concretes;

import com.banking.business.abstracts.AuthService;
import com.banking.business.constants.Messages;
import com.banking.business.dtos.requests.LoginRequest;
import com.banking.business.dtos.requests.RegisterCorporateRequest;
import com.banking.business.dtos.requests.RegisterIndividualRequest;
import com.banking.business.dtos.responses.JwtResponse;
import com.banking.business.rules.AuthBusinessRules;
import com.banking.core.security.jwt.JwtTokenProvider;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.core.utilities.results.SuccessResult;
import com.banking.entities.CorporateCustomer;
import com.banking.entities.IndividualCustomer;
import com.banking.entities.User;
import com.banking.entities.enums.Role;
import com.banking.repositories.abstracts.CorporateCustomerRepository;
import com.banking.repositories.abstracts.IndividualCustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthManager implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final IndividualCustomerRepository individualCustomerRepository;
    private final CorporateCustomerRepository corporateCustomerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthBusinessRules rules;

    @Override
    public DataResult<JwtResponse> login(LoginRequest loginRequest) {
        rules.checkIfEmailNotExists(loginRequest.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse response = new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles);
        return new SuccessDataResult<>(response, Messages.Auth.LOGIN_SUCCESS);
    }

    @Override
    public Result registerIndividual(RegisterIndividualRequest registerRequest) {
        rules.checkIfEmailExists(registerRequest.getEmail());

        IndividualCustomer customer = new IndividualCustomer();
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        customer.setIdentityNumber(registerRequest.getIdentityNumber());
        customer.setNationality(registerRequest.getNationality());
        customer.setBirthDate(registerRequest.getBirthDate());
        customer.setStatus(true);
        customer.setCustomerNumber("IND" + System.currentTimeMillis());
        customer.setCreatedDate(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_INDIVIDUAL_CUSTOMER);
        customer.setRoles(roles);

        individualCustomerRepository.save(customer);
        return new SuccessResult(Messages.Auth.REGISTER_SUCCESS);
    }

    @Override
    public Result registerCorporate(RegisterCorporateRequest registerRequest) {
        rules.checkIfEmailExists(registerRequest.getEmail());

        CorporateCustomer customer = new CorporateCustomer();
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setCompanyName(registerRequest.getCompanyName());
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        customer.setTaxNumber(registerRequest.getTaxNumber());
        customer.setStatus(true);
        customer.setCustomerNumber("CORP" + System.currentTimeMillis());
        customer.setCreatedDate(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_CORPORATE_CUSTOMER);
        customer.setRoles(roles);

        corporateCustomerRepository.save(customer);
        return new SuccessResult(Messages.Auth.REGISTER_SUCCESS);
    }
} 