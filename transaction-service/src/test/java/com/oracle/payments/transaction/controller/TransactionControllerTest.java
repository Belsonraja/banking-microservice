package com.oracle.payments.transaction.controller;

import static com.oracle.payments.transaction.constant.TransactionConstant.CREDIT_SUCCESS;
import static com.oracle.payments.transaction.constant.TransactionConstant.DEBIT_SUCCESS;
import static org.mockito.Mockito.doReturn;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.oracle.payments.transaction.model.AuthenticationRequest;
import com.oracle.payments.transaction.model.AuthenticationResponse;
import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;
import com.oracle.payments.transaction.security.JwtUtil;
import com.oracle.payments.transaction.service.TransactionServiceImpl;
import com.oracle.payments.transaction.service.TransactionUserDetailsService;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerTest {
	@InjectMocks
	private TransactionController transactionController;
	
	@Mock
	private TransactionServiceImpl transactionService;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private TransactionUserDetailsService transactionUserDetailsService;
	
	@Mock
	private Authentication authenticate;
	
	@Mock
	private UserDetails userDetails;
	
	@Mock
	private JwtUtil jwtUtil;
	
	@Test
	public void creditAmount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.CREATED, String.format(CREDIT_SUCCESS, transaction.getAmount(), transaction.getAccountId(), 10000));
		doReturn(expectedResponse).when(transactionService).creditAmount(Mockito.any());
		ResponseEntity<TransactionResponse> controllerResponse = transactionController.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}
	
	@Test
	public void debitAmount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.CREATED, String.format(DEBIT_SUCCESS, transaction.getAmount(), transaction.getAccountId(), 10000));
		doReturn(expectedResponse).when(transactionService).debitAmount(Mockito.any());
		ResponseEntity<TransactionResponse> controllerResponse = transactionController.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}
	
	@Test
	public void getBalance() {
		Integer accountId = 1;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.OK, String.valueOf(1000));
		doReturn(expectedResponse).when(transactionService).getBalance(Mockito.any());
		ResponseEntity<TransactionResponse> controllerResponse = transactionController.getBalance(accountId);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}
	
	@Test
	public void createAuthenticationToken() {
		String jwt = "djho940#dljfddfdfd";
		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		AuthenticationResponse expectedResponse = new AuthenticationResponse(jwt);
		doReturn(authenticate).when(authenticationManager).authenticate(Mockito.any());
		doReturn(userDetails).when(transactionUserDetailsService).loadUserByUsername(Mockito.any());
		doReturn(jwt).when(jwtUtil).generateToken(Mockito.any());
		ResponseEntity<AuthenticationResponse> controllerResponse = (ResponseEntity<AuthenticationResponse>) transactionController.createAuthenticationToken(authenticationRequest);
		Assert.assertEquals(expectedResponse.getJwt(), controllerResponse.getBody().getJwt());
	}
	
	@Test
	public void health() {
		String expectedResponse = "Service is up!";
		ResponseEntity<String> controllerResponse = transactionController.health();
		Assert.assertEquals(expectedResponse, controllerResponse.getBody());
	}
	
}
