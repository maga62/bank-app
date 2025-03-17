package com.banking.business.concretes;

import com.banking.business.abstracts.CreditApplicationTrackingService;
import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CreditApplicationTrackingManager implements CreditApplicationTrackingService {

    @Override
    public Map<CreditApplicationStatus, List<CreditApplication>> groupApplicationsByStatus(List<CreditApplication> applications) {
        return applications.stream()
                .collect(Collectors.groupingBy(app -> convertStatus(app.getStatus())));
    }
    
    private CreditApplicationStatus convertStatus(CreditApplication.Status status) {
        return CreditApplicationStatus.valueOf(status.name());
    }
} 