package com.example.bank_transfer_app.service;

import com.example.bank_transfer_app.exception.AccountNotFoundException;
import com.example.bank_transfer_app.exception.BankSystemException;
import com.example.bank_transfer_app.exception.InsufficientBalanceException;
import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.model.TransactionStatus;
import com.example.bank_transfer_app.repository.BankTransactionRepository;
import com.example.bank_transfer_app.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankService {

    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;
    private final TransactionAuditService auditService;

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.countAccounts(customer.getAccountNumber()) > 0) {
            throw new BankSystemException("Customer with account number " + customer.getAccountNumber() + " already exists.");
        }
        if (customerRepository.countEmails(customer.getEmail()) > 0) {
            throw new BankSystemException("Customer with email " + customer.getEmail() + " already exists.");
        }
        return customerRepository.save(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(String fromAccount, String toAccount, BigDecimal amount, boolean simulateFailure) {
        
    
        if (fromAccount.equals(toAccount)) {
            throw new BankSystemException("Source and destination accounts must be different.");
        }

        Customer sender = customerRepository.getByAccountNumber(fromAccount)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found: " + fromAccount));
        
        Customer receiver = customerRepository.getByAccountNumber(toAccount)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found: " + toAccount));

    
        int updatedRows = customerRepository.debit(fromAccount, amount);
        if (updatedRows == 0) {
            throw new InsufficientBalanceException("Insufficient balance in account " + fromAccount);
        }

    
        if (simulateFailure) {
            throw new BankSystemException("Simulated system failure after the withdrawal.");
        }

    
        customerRepository.credit(toAccount, amount);


        BankTransaction successTx = new BankTransaction();
        successTx.setFromAccount(fromAccount);
        successTx.setToAccount(toAccount);
        successTx.setAmount(amount);
        successTx.setStatus(TransactionStatus.SUCCESS);
        successTx.setDescription("Transfer completed");
        successTx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(successTx);
    }
}