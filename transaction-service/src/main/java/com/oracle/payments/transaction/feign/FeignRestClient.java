package com.oracle.payments.transaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;

@FeignClient(name = "accounts-service")
public interface FeignRestClient {
	@PostMapping(value = "accounts/checkAccountBalance")
	public ResponseEntity<AccountsResponse> getAccountBalance(@RequestBody Integer accountId);
	
	@PostMapping(value = "accounts/checkAccount")
	public ResponseEntity<AccountsResponse> checkAccountById(@RequestBody Integer accountId);
	
	@PostMapping(value = "accounts/validateAccount")
	public ResponseEntity<AccountsResponse> validateAccount(@RequestBody AccountsRequest account);
}
