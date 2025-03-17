package com.banking.business.abstracts;

import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import java.util.List;
import java.util.Map;

public interface CreditApplicationTrackingService {
    Map<CreditApplicationStatus, List<CreditApplication>> groupApplicationsByStatus(List<CreditApplication> applications);
} 