package com.oracle.payments.transaction.exception;

public class TransactionServiceException extends Exception {

	private static final long serialVersionUID = 2177107666882813361L;

	public TransactionServiceException() {
		super();
	}

	public TransactionServiceException(final String message) {
		super(message);
	}
}
