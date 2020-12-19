package com.oracle.payments.accounts.kafka;

import com.oracle.payments.transaction.model.Transaction;

public interface KafkaService {
	public void consume(Transaction message);
}
