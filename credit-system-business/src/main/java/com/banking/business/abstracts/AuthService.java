package com.banking.business.abstracts;

import com.banking.business.dtos.requests.LoginRequest;
import com.banking.business.dtos.requests.RegisterCorporateRequest;
import com.banking.business.dtos.requests.RegisterIndividualRequest;
import com.banking.business.dtos.responses.JwtResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;

public interface AuthService {
    DataResult<JwtResponse> login(LoginRequest loginRequest);
    Result registerIndividual(RegisterIndividualRequest registerRequest);
    Result registerCorporate(RegisterCorporateRequest registerRequest);
} 