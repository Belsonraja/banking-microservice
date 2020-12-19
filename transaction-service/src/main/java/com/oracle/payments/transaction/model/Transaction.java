package com.oracle.payments.transaction.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
	private Integer accountId;
	private double amount;
}
