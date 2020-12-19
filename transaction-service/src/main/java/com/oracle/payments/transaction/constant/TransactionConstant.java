package com.oracle.payments.transaction.constant;

import org.springframework.stereotype.Component;

@Component
public interface TransactionConstant {
	public static final String INVALID_AMOUNT = "Invalid Transaction Amount!";
	public static final String INVALID_ACCOUNT = "Invalid Account!";
	public static final String ACCOUNT_ID_ISNULL = "Account Id should be provided!";
	public static final String TXN_REQUEST_ISNULL = "Invalid Request!";
	public static final String TRANSACTION_FAILED = "Transaction Failed!";
	public static final String UNABLE_TO_GET_ACC_BALANCE = "Unable to retrieve the account balance.";
	public static final String INSUFFICIENT_BALANCE = "Insufficient account balance. Your available balance is INR %f";
	public static final String DEBIT_SUCCESS = "INR %s is successfully debited from Account Id %d and your new account balance is INR %s";
	public static final String CREDIT_SUCCESS = "INR %s is successfully credited to Account Id %d and your new account balance is INR %s";
	public static final String ACCOUNT_VALIDATION_SUCESSS = "VALID";
	public static final String ACCOUNT_VALIDATION_FAILURE = "INVALID";
	public static final String SUCESSS = "SUCESSS";
	public static final String FAILURE = "FAILURE";
}
