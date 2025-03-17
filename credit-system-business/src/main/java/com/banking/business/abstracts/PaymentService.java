package com.banking.business.abstracts;

import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.dtos.request.ProcessPaymentRequest;
import com.banking.core.dtos.response.PaymentStatusResponse;

public interface PaymentService {
    Result processPayment(ProcessPaymentRequest request);
    DataResult<PaymentStatusResponse> getPaymentStatus(Long paymentId);
    Result cancelPayment(Long paymentId);
    Result refundPayment(Long paymentId, String reason);
} 