package com.oracle.payments.transaction.kafka;

import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;

public interface KafkaService {
	public TransactionResponse send(Transaction message);
}
