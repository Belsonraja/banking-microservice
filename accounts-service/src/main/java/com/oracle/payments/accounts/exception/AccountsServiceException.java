package com.oracle.payments.accounts.exception;

public class AccountsServiceException extends Exception {

	private static final long serialVersionUID = 2177107666882813361L;

	public AccountsServiceException() {
		super();
	}

	public AccountsServiceException(final String message) {
		super(message);
	}
}
