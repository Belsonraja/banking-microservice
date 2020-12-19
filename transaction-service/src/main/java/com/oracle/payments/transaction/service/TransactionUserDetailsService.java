package com.oracle.payments.transaction.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.transaction.feign.FeignRestClient;
import com.oracle.payments.transaction.util.PasswordUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionUserDetailsService implements UserDetailsService{
	
	@Autowired
	private FeignRestClient feignClient;
	
	@Value(value = "${url.accounts-service.validate-account}")
	private String validateAccountUrl;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AccountsRequest account = new AccountsRequest();
		String splitUsername;
		String splitPassword;
		String actualPassword;
		
		try {
			splitUsername = username.split("~")[0].toUpperCase();
			actualPassword = username.split("~")[1];
			splitPassword = PasswordUtil.generateHash(username.split("~")[1]);
			account.setName(splitUsername);
			account.setPassword(splitPassword);
			log.info(account.getName()+"-"+account.getPassword());
			//ResponseEntity<AccountsResponse> result = restTemplate.postForEntity(validateAccountUrl, account, AccountsResponse.class);
			ResponseEntity<AccountsResponse> result = feignClient.validateAccount(account);
			log.info(result.getBody() != null ? result.getBody().getResponse() : "No response from service...");
		} catch (NoSuchAlgorithmException e) {
			throw new UsernameNotFoundException("Bad Request", e);
		}
		
		return new User(splitUsername, actualPassword, new ArrayList<>());
	}

}
