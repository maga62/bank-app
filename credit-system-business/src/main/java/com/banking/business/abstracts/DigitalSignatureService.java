package com.banking.business.abstracts;

import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.dtos.request.SignLoanAgreementRequest;
import com.banking.core.dtos.response.SignedAgreementResponse;

public interface DigitalSignatureService {
    Result signLoanAgreement(SignLoanAgreementRequest request);
    DataResult<SignedAgreementResponse> getSignedAgreement(Long customerId, Long agreementId);
    Result verifySignature(Long agreementId);
} 