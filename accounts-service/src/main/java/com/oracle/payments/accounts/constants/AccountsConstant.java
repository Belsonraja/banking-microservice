package com.oracle.payments.accounts.constants;

import org.springframework.stereotype.Component;

@Component
public interface AccountsConstant {
	public static final String NO_ACCOUNT_NAME_PROVIDED = "Account Name should be provided!";
	public static final String ACCOUNT_ALREADY_EXISTS = "Account already exists!";
	public static final String ACCOUNT_NOT_CREATED = "Account not created!";
	public static final String ACCOUNT_CREATED = "Account created successfully! Your Account Id is %d and Account Name is %s";
	public static final String ACCOUNT_VALIDATION_SUCESSS = "VALID";
	public static final String ACCOUNT_VALIDATION_FAILURE = "INVALID";
	public static final String INVALID_ACCOUNT = "Invalid Account!";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String ACCOUNT_DELETED = "Account deleted successfully!";
}
