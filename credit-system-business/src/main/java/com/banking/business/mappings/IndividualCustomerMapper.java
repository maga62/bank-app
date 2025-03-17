package com.banking.business.mappings;

import com.banking.business.dtos.requests.CreateIndividualCustomerRequest;
import com.banking.business.dtos.requests.UpdateIndividualCustomerRequest;
import com.banking.business.dtos.responses.IndividualCustomerResponse;
import com.banking.business.mappings.config.MapStructConfig;
import com.banking.entities.IndividualCustomer;
import com.banking.entities.enums.Role;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(config = MapStructConfig.class, imports = {HashSet.class, Role.class})
public interface IndividualCustomerMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "status", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "roles", expression = "java(getDefaultRoles())")
    IndividualCustomer toEntity(CreateIndividualCustomerRequest request);
    
    @Mapping(target = "customerNumber", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "deletedDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UpdateIndividualCustomerRequest request, @MappingTarget IndividualCustomer customer);
    
    IndividualCustomerResponse toResponse(IndividualCustomer customer);
    
    List<IndividualCustomerResponse> toResponseList(List<IndividualCustomer> customers);
    
    default Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_INDIVIDUAL_CUSTOMER);
        return roles;
    }
} 