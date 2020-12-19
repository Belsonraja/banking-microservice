package com.oracle.payments.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.payments.transaction.model.AuthenticationRequest;
import com.oracle.payments.transaction.model.AuthenticationResponse;
import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;
import com.oracle.payments.transaction.security.JwtUtil;
import com.oracle.payments.transaction.service.TransactionService;
import com.oracle.payments.transaction.service.TransactionUserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TransactionUserDetailsService transactionUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private TransactionService transactionService;

	@GetMapping(value = "/health")
	public ResponseEntity<String> health() {
		return new ResponseEntity<>("Service is up!", HttpStatus.OK);
	}

	@PostMapping(value = "/creditAmount")
	public ResponseEntity<TransactionResponse> creditAmount(@RequestBody Transaction transaction) {
		TransactionResponse response = transactionService.creditAmount(transaction);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@PostMapping(value = "/getBalance")
	public ResponseEntity<TransactionResponse> getBalance(@RequestBody Integer accountId) {
		TransactionResponse response = transactionService.getBalance(accountId);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@PostMapping(value = "/debitAmount")
	public ResponseEntity<TransactionResponse> debitAmount(@RequestBody Transaction transaction) {
		TransactionResponse response = transactionService.debitAmount(transaction);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@PostMapping(value = "/authenticate")
	public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
			@RequestBody AuthenticationRequest authenticationRequest) {
		
		String password = authenticationRequest.getPassword();
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername()+"~"+password, password));
		} catch (AuthenticationException e) {
			log.error("Error while authenticating user - Incorrect Username or password.");
			throw new BadCredentialsException("Incorrect Username or password", e);
		}
		
		final UserDetails userDetails = transactionUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername()+"~"+password);
		final String jwt = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
}
