package com.oracle.payments.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.accounts.service.AccountsService;

@RestController
@RequestMapping(value = "/accounts")
public class AccountsController {

	@Autowired
	private AccountsService accountsService;
	
	@PostMapping(value = "/createAccount")
	public ResponseEntity<AccountsResponse> createAccount(@RequestBody AccountsRequest account) {
		AccountsResponse response = accountsService.createAccount(account);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
	@PostMapping(value = "/validateAccount")
	public ResponseEntity<AccountsResponse> validateAccount(@RequestBody AccountsRequest account) {
		AccountsResponse response = accountsService.validateAccount(account);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
	@PostMapping(value = "/checkAccount")
	public ResponseEntity<AccountsResponse> checkAccountById(@RequestBody Integer accountId) {
		AccountsResponse response = accountsService.checkAccountById(accountId);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
	@PostMapping(value = "/checkAccountBalance")
	public ResponseEntity<AccountsResponse> getAccountBalance(@RequestBody Integer accountId) {
		AccountsResponse response = accountsService.getAccountBalance(accountId);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
}
