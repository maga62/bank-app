package com.banking.business.abstracts;

import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.OtpVerification;

public interface OTPService {
    DataResult<OtpVerification> generateOTP(Long userId, String channel);
    Result verifyOTP(Long userId, String otpCode);
    Result resendOTP(Long userId, String channel);
    Result invalidateOTP(Long userId);
    void cleanupExpiredOTPs();
} 