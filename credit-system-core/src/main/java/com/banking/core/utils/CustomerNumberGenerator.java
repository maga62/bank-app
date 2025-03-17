package com.banking.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CustomerNumberGenerator {
    public static String generateCustomerNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return String.format("%s%03d", timestamp, randomNumber);
    }
} 