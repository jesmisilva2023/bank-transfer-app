package com.example.bank_transfer_app.config;

import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final BankService bankService;

    @Override
    public void run(String... args) throws Exception {

        try {
            if (bankService.createCustomer(new Customer("100001", "Kamal", new BigDecimal("50000.00"), "kamal@example.com")) != null) {
                bankService.createCustomer(new Customer("100002", "Nimali", new BigDecimal("25000.00"), "nimali@example.com"));
                bankService.createCustomer(new Customer("100003", "Saman", new BigDecimal("10000.00"), "saman@example.com"));
            }
        } catch (Exception e) {
        
        }
    }
}