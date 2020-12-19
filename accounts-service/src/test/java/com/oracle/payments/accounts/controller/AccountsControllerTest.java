package com.oracle.payments.accounts.controller;

import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_CREATED;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_VALIDATION_SUCESSS;
import static org.mockito.Mockito.doReturn;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.accounts.service.AccountsServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class AccountsControllerTest {
	
	@InjectMocks
	private AccountsController accountsController;
	
	@Mock
	private AccountsServiceImpl accountsService;
	
	@Test
	public void createAccount() {
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.CREATED, String.format(ACCOUNT_CREATED, 1, account.getName().toUpperCase()));
		doReturn(expectedResponse).when(accountsService).createAccount(account);
		ResponseEntity<AccountsResponse> controllerResponse = accountsController.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}
	
	@Test
	public void checkAccountById() {
		Integer account = 1;
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.OK, ACCOUNT_VALIDATION_SUCESSS);
		doReturn(expectedResponse).when(accountsService).checkAccountById(account);
		ResponseEntity<AccountsResponse> controllerResponse = accountsController.checkAccountById(account);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}
	
	@Test
	public void validateAccount() {
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.OK, ACCOUNT_VALIDATION_SUCESSS);
		doReturn(expectedResponse).when(accountsService).validateAccount(account);
		ResponseEntity<AccountsResponse> controllerResponse = accountsController.validateAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), controllerResponse.getBody().getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), controllerResponse.getBody().getStatus());
	}

}
