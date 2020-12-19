package com.oracle.payments.accounts.service;

import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;

public interface AccountsService {
	public boolean checkAccount(String accountName);
	public AccountsResponse checkAccountById(Integer accountId);
	public AccountsResponse createAccount(AccountsRequest account);
	public AccountsResponse validateAccount(AccountsRequest account);
	public AccountsResponse getAccountBalance(Integer accountId);
}
