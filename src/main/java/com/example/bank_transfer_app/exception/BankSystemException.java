package com.example.bank_transfer_app.exception;

public class BankSystemException extends RuntimeException {
    public BankSystemException(String message) {
        super(message);
    }
}