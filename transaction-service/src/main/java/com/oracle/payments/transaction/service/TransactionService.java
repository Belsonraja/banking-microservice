package com.oracle.payments.transaction.service;

import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;

public interface TransactionService {
	public TransactionResponse creditAmount(Transaction transaction);
	public TransactionResponse debitAmount(Transaction transaction);
	public TransactionResponse getBalance(Integer accountId);
}
