package com.banking.business.concretes;

import com.banking.business.dtos.requests.CreateIndividualCustomerRequest;
import com.banking.business.dtos.responses.IndividualCustomerResponse;
import com.banking.business.rules.IndividualCustomerBusinessRules;
import com.banking.core.utilities.mappers.ModelMapperService;
import com.banking.core.utilities.results.DataResult;
import com.banking.entities.IndividualCustomer;
import com.banking.repositories.abstracts.IndividualCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IndividualCustomerManagerTest {

    @Mock
    private IndividualCustomerRepository repository;

    @Mock
    private IndividualCustomerBusinessRules rules;

    @Mock
    private ModelMapperService mapper;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private IndividualCustomerManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mapper.forRequest()).thenReturn(modelMapper);
        when(mapper.forResponse()).thenReturn(modelMapper);
    }

    @Test
    void add_ShouldReturnSuccessDataResult_WhenValidRequest() {
        // Arrange
        CreateIndividualCustomerRequest request = new CreateIndividualCustomerRequest();
        request.setIdentityNumber("12345678901");
        request.setFirstName("John");
        request.setLastName("Doe");
        
        IndividualCustomer customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setIdentityNumber("12345678901");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCustomerNumber("IND123456");
        
        IndividualCustomerResponse response = new IndividualCustomerResponse();
        response.setId(1L);
        response.setIdentityNumber("12345678901");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setCustomerNumber("IND123456");
        
        when(modelMapper.map(request, IndividualCustomer.class)).thenReturn(customer);
        when(repository.save(any(IndividualCustomer.class))).thenReturn(customer);
        when(modelMapper.map(customer, IndividualCustomerResponse.class)).thenReturn(response);
        
        // Act
        DataResult<IndividualCustomerResponse> result = manager.add(request);
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(response, result.getData());
        verify(rules).checkIfIdentityNumberExists(request.getIdentityNumber());
        verify(repository).save(any(IndividualCustomer.class));
    }

    @Test
    void getByIdentityNumber_ShouldReturnSuccessDataResult_WhenCustomerExists() {
        // Arrange
        String identityNumber = "12345678901";
        
        IndividualCustomer customer = new IndividualCustomer();
        customer.setId(1L);
        customer.setIdentityNumber(identityNumber);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setCustomerNumber("IND123456");
        
        IndividualCustomerResponse response = new IndividualCustomerResponse();
        response.setId(1L);
        response.setIdentityNumber(identityNumber);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setCustomerNumber("IND123456");
        
        //when(repository.findByIdentityNumber(identityNumber)).thenReturn(customer);
        when(modelMapper.map(customer, IndividualCustomerResponse.class)).thenReturn(response);
        
        // Act
        DataResult<IndividualCustomerResponse> result = manager.getByIdentityNumber(identityNumber);
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(response, result.getData());
        verify(repository).findByIdentityNumber(identityNumber);
    }
} 