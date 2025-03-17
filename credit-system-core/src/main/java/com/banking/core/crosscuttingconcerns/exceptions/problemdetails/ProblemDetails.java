package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetails {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private Map<String, Object> extensions;

    public ProblemDetails(String title, int status, String detail) {
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public ProblemDetails(String title) {
        this.title = title;
    }
    
} 